package cn.edu.hqu.databackup.manager.group;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.config.NacosSwitchConfig;
import cn.edu.hqu.databackup.config.SwitchConfig;
import cn.edu.hqu.databackup.dao.group.GroupEntityService;
import cn.edu.hqu.databackup.dao.group.GroupMemberEntityService;
import cn.edu.hqu.databackup.dao.user.UserAcproblemEntityService;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.api.pojo.entity.group.GroupMember;
import cn.edu.hqu.api.pojo.entity.user.UserAcproblem;
import cn.edu.hqu.databackup.pojo.vo.AccessVO;
import cn.edu.hqu.databackup.pojo.vo.GroupVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.utils.RedisUtils;
import cn.edu.hqu.databackup.validator.GroupValidator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Component
public class GroupManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private UserAcproblemEntityService userAcproblemEntityService;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private NacosSwitchConfig nacosSwitchConfig;

    public IPage<GroupVO> getGroupList(Integer limit, Integer currentPage, String keyword, Integer auth, boolean onlyMine) {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        if (auth == null || auth < 1) auth = 0;

        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
        }

        String uid = "";
        boolean isRoot = false;
        if (userRolesVo != null) {
            uid = userRolesVo.getUid();
            isRoot = SecurityUtils.getSubject().hasRole("root");
        }
        return groupEntityService.getGroupList(limit, currentPage, keyword, auth, uid, onlyMine, isRoot);
    }

    public Group getGroup(Long gid) throws StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);
        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("?????????????????????????????????????????????????????????");
        }
        if (!group.getVisible() && !isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("????????????????????????????????????????????????");
        }

        if (!isRoot && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            group.setCode(null);
        }

        return group;
    }

    public AccessVO getGroupAccess(Long gid) throws StatusFailException, StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        boolean access = false;

        if (groupValidator.isGroupMember(userRolesVo.getUid(), gid) || isRoot) {
            access = true;
            Group group = groupEntityService.getById(gid);
            if (group == null || group.getStatus() == 1 && !isRoot) {
                throw new StatusNotFoundException("?????????????????????????????????????????????????????????");
            }
            if (!isRoot && !group.getVisible() && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("????????????????????????????????????????????????");
            }
        }

        AccessVO accessVo = new AccessVO();
        accessVo.setAccess(access);
        return accessVo;
    }

    public Integer getGroupAuth(Long gid) {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).eq("uid", userRolesVo.getUid());

        GroupMember groupMember = groupMemberEntityService.getOne(groupMemberQueryWrapper);

        Integer auth = 0;
        if (groupMember != null) {
            auth = groupMember.getAuth();
        }
        return auth;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addGroup(Group group) throws StatusFailException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        if (!isRoot && !isAdmin && !isProblemAdmin) {

            QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
            int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);
            SwitchConfig switchConfig = nacosSwitchConfig.getSwitchConfig();
            if (userAcProblemCount < switchConfig.getDefaultCreateGroupACInitValue()) {
                throw new StatusForbiddenException("????????????????????????????????????????????????????????????????????????" +
                        switchConfig.getDefaultCreateGroupACInitValue() + "?????????!");
            }

            String lockKey = Constants.Account.GROUP_ADD_NUM_LOCK.getCode() + userRolesVo.getUid();
            Integer num = (Integer) redisUtils.get(lockKey);
            if (num == null) {
                redisUtils.set(lockKey, 1, 3600 * 24);
            } else if (num >= switchConfig.getDefaultCreateGroupDailyLimit()) {
                throw new StatusForbiddenException("????????????????????????????????????????????????" + switchConfig.getDefaultCreateGroupDailyLimit() + "?????????????????????");
            } else {
                redisUtils.incr(lockKey, 1);
            }

            QueryWrapper<Group> existedGroupQueryWrapper = new QueryWrapper<>();
            existedGroupQueryWrapper.eq("owner", userRolesVo.getUsername());
            int existedGroupNum = groupEntityService.count(existedGroupQueryWrapper);

            if (existedGroupNum >= switchConfig.getDefaultCreateGroupLimit()) {
                throw new StatusForbiddenException("?????????????????????????????????" + switchConfig.getDefaultCreateGroupLimit() + "?????????????????????????????????????????????");
            }

        }
        group.setOwner(userRolesVo.getUsername());
        group.setUid(userRolesVo.getUid());

        if (!StringUtils.isEmpty(group.getName()) && (group.getName().length() < 5 || group.getName().length() > 25)) {
            throw new StatusFailException("??????????????????????????? 5 ??? 25???");
        }

        if (!StringUtils.isEmpty(group.getShortName()) && (group.getShortName().length() < 5 || group.getShortName().length() > 10)) {
            throw new StatusFailException("??????????????????????????? 5 ??? 10???");
        }

        if (!StringUtils.isEmpty(group.getBrief()) && (group.getBrief().length() < 5 || group.getBrief().length() > 50)) {
            throw new StatusFailException("??????????????????????????? 5 ??? 50???");
        }

        if (group.getAuth() == null || group.getAuth() < 1 || group.getAuth() > 3) {
            throw new StatusFailException("?????????????????????????????????1~3???");
        }

        if (group.getAuth() == 2 || group.getAuth() == 3) {
            if (StringUtils.isEmpty(group.getCode()) || group.getCode().length() != 6) {
                throw new StatusFailException("?????????????????????????????????????????? 6???");
            }
        }

        if (!StringUtils.isEmpty(group.getDescription()) && (group.getDescription().length() < 5 || group.getDescription().length() > 1000)) {
            throw new StatusFailException("??????????????????????????? 5 ??? 1000???");
        }

        QueryWrapper<Group> groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("name", group.getName());
        int sameNameGroupCount = groupEntityService.count(groupQueryWrapper);
        if (sameNameGroupCount > 0) {
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("short_name", group.getShortName());
        int sameShortNameGroupCount = groupEntityService.count(groupQueryWrapper);
        if (sameShortNameGroupCount > 0) {
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        boolean isOk = groupEntityService.save(group);
        if (!isOk) {
            throw new StatusFailException("?????????????????????????????????");
        } else {
            groupMemberEntityService.save(new GroupMember()
                    .setUid(userRolesVo.getUid())
                    .setGid(group.getId())
                    .setAuth(5));
        }
    }

    public void updateGroup(Group group) throws StatusFailException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), group.getId()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (!StringUtils.isEmpty(group.getName()) && (group.getName().length() < 5 || group.getName().length() > 25)) {
            throw new StatusFailException("??????????????????????????? 5 ??? 25???");
        }

        if (!StringUtils.isEmpty(group.getShortName()) && (group.getShortName().length() < 5 || group.getShortName().length() > 10)) {
            throw new StatusFailException("??????????????????????????? 5 ??? 10???");
        }

        if (!StringUtils.isEmpty(group.getBrief()) && (group.getBrief().length() < 5 || group.getBrief().length() > 50)) {
            throw new StatusFailException("??????????????????????????? 5 ??? 50???");
        }

        if (!StringUtils.isEmpty(group.getCode()) && group.getCode().length() != 6) {
            throw new StatusFailException("?????????????????????????????? 6???");
        }

        if (!StringUtils.isEmpty(group.getDescription()) && (group.getDescription().length() < 5 || group.getDescription().length() > 1000)) {
            throw new StatusFailException("??????????????????????????? 5 ??? 1000???");
        }

        QueryWrapper<Group> groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("name", group.getName())
                .ne("id", group.getId());
        int sameNameGroupCount = groupEntityService.count(groupQueryWrapper);
        if (sameNameGroupCount > 0) {
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("short_name", group.getShortName())
                .ne("id", group.getId());
        int sameShortNameGroupCount = groupEntityService.count(groupQueryWrapper);
        if (sameShortNameGroupCount > 0) {
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        boolean isOk = groupEntityService.updateById(group);
        if (!isOk) {
            throw new StatusFailException("?????????????????????????????????");
        }
    }

    public void deleteGroup(Long gid) throws StatusFailException, StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!isRoot && !userRolesVo.getUid().equals(group.getUid())) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).in("auth", 3, 4, 5);
        List<GroupMember> groupMemberList = groupMemberEntityService.list(groupMemberQueryWrapper);
        List<String> groupMemberUidList = groupMemberList.stream()
                .map(GroupMember::getUid)
                .collect(Collectors.toList());

        boolean isOk = groupEntityService.removeById(gid);
        if (!isOk) {
            throw new StatusFailException("?????????????????????????????????");
        } else {
            groupMemberEntityService.addDissolutionNoticeToGroupMember(gid,
                    group.getName(),
                    groupMemberUidList,
                    userRolesVo.getUsername());
        }
    }
}
