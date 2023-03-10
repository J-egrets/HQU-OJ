package cn.edu.hqu.databackup.manager.group.discussion;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.dao.discussion.DiscussionEntityService;
import cn.edu.hqu.databackup.dao.group.GroupEntityService;
import cn.edu.hqu.databackup.dao.problem.ProblemEntityService;
import cn.edu.hqu.databackup.dao.user.UserAcproblemEntityService;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.api.pojo.entity.user.UserAcproblem;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.utils.RedisUtils;
import cn.edu.hqu.databackup.validator.CommonValidator;
import cn.edu.hqu.databackup.validator.GroupValidator;

/**
 * @Author: egret
 */
@Component
public class GroupDiscussionManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private DiscussionEntityService discussionEntityService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private UserAcproblemEntityService userAcproblemEntityService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CommonValidator commonValidator;

    public IPage<Discussion> getDiscussionList(Integer limit,
                                               Integer currentPage,
                                               Long gid,
                                               String pid) throws StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????????????????");
        }

        if (!groupValidator.isGroupMember(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(pid)) {
            discussionQueryWrapper.eq("pid", pid);
        }

        IPage<Discussion> iPage = new Page<>(currentPage, limit);

        discussionQueryWrapper
                .eq("status", 0)
                .eq("gid", gid)
                .orderByDesc("top_priority")
                .orderByDesc("gmt_create")
                .orderByDesc("like_num")
                .orderByDesc("view_num");

        return discussionEntityService.page(iPage, discussionQueryWrapper);
    }

    public IPage<Discussion> getAdminDiscussionList(Integer limit, Integer currentPage, Long gid) throws StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????????????????");
        }

        if (!isRoot && !groupValidator.isGroupAdmin(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();

        IPage<Discussion> iPage = new Page<>(currentPage, limit);

        discussionQueryWrapper
                .eq("gid", gid)
                .orderByDesc("top_priority")
                .orderByDesc("gmt_create")
                .orderByDesc("like_num")
                .orderByDesc("view_num");

        return discussionEntityService.page(iPage, discussionQueryWrapper);
    }

    public void addDiscussion(Discussion discussion) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {

        commonValidator.validateContent(discussion.getTitle(), "????????????", 255);
        commonValidator.validateContent(discussion.getDescription(), "????????????", 255);
        commonValidator.validateContent(discussion.getContent(), "??????", 65535);
        commonValidator.validateNotEmpty(discussion.getCategoryId(), "????????????");
        commonValidator.validateNotEmpty(discussion.getGid(), "??????????????????ID");

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Long gid = discussion.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        String problemId = discussion.getPid();
        if (problemId != null) {
            QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.eq("problem_id", problemId);
            Problem problem = problemEntityService.getOne(problemQueryWrapper);

            if (problem == null) {
                throw new StatusNotFoundException("??????????????????");
            } else if (problem.getIsGroup()) {
                discussion.setGid(problem.getGid());
            }
        }

        if (!isRoot && !isProblemAdmin && !isAdmin) {
            QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
            int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);

            if (userAcProblemCount < 10) {
                throw new StatusForbiddenException("???????????????????????????????????????????????????????????????10?????????!");
            }

            String lockKey = Constants.Account.DISCUSSION_ADD_NUM_LOCK.getCode() + userRolesVo.getUid();
            Integer num = (Integer) redisUtils.get(lockKey);
            if (num == null) {
                redisUtils.set(lockKey, 1, 3600 * 24);
            } else if (num >= 5) {
                throw new StatusForbiddenException("??????????????????????????????????????????5?????????????????????");
            } else {
                redisUtils.incr(lockKey, 1);
            }
        }

        discussion.setAuthor(userRolesVo.getUsername())
                .setAvatar(userRolesVo.getAvatar())
                .setUid(userRolesVo.getUid());

        if (groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            discussion.setRole("root");
        } else if (groupValidator.isGroupAdmin(userRolesVo.getUid(), gid)) {
            discussion.setRole("admin");
        } else {
            discussion.setTopPriority(false);
        }

        boolean isOk = discussionEntityService.save(discussion);
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }

    public void updateDiscussion(Discussion discussion) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {

        commonValidator.validateNotEmpty(discussion.getId(), "??????ID");
        commonValidator.validateContent(discussion.getTitle(), "????????????", 255);
        commonValidator.validateContent(discussion.getDescription(), "????????????", 255);
        commonValidator.validateContent(discussion.getContent(), "??????", 65535);
        commonValidator.validateNotEmpty(discussion.getCategoryId(), "????????????");

        QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
        discussionQueryWrapper
                .select("id", "uid", "gid")
                .eq("id", discussion.getId());

        Discussion oriDiscussion = discussionEntityService.getOne(discussionQueryWrapper);
        if (oriDiscussion == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = oriDiscussion.getGid();
        if (gid == null) {
            throw new StatusNotFoundException("??????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid)
                && !oriDiscussion.getUid().equals(userRolesVo.getUid())
                && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
        discussionUpdateWrapper.set("title", discussion.getTitle())
                .set("content", discussion.getContent())
                .set("description", discussion.getDescription())
                .set("category_id", discussion.getCategoryId())
                .set(isRoot || isProblemAdmin || isAdmin,
                        "top_priority", discussion.getTopPriority())
                .eq("id", discussion.getId());

        boolean isOk = discussionEntityService.update(discussionUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }

    public void deleteDiscussion(Long did) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Discussion discussion = discussionEntityService.getById(did);

        Long gid = discussion.getGid();
        if (gid == null) {
            throw new StatusNotFoundException("??????????????????????????????????????????");
        }
        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !discussion.getUid().equals(userRolesVo.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = discussionEntityService.removeById(did);
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }
}
