package cn.edu.hqu.databackup.manager.group.contest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.dao.contest.ContestEntityService;
import cn.edu.hqu.databackup.dao.contest.ContestRegisterEntityService;
import cn.edu.hqu.databackup.dao.group.GroupContestEntityService;
import cn.edu.hqu.databackup.dao.group.GroupEntityService;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.contest.ContestRegister;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.databackup.pojo.vo.AdminContestVO;
import cn.edu.hqu.databackup.pojo.vo.ContestAwardConfigVO;
import cn.edu.hqu.databackup.pojo.vo.ContestVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.validator.ContestValidator;
import cn.edu.hqu.databackup.validator.GroupValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @Author: egret
 */
@Component
public class GroupContestManager {
    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private GroupContestEntityService groupContestEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private ContestRegisterEntityService contestRegisterEntityService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private ContestValidator contestValidator;

    public IPage<ContestVO> getContestList(Integer limit, Integer currentPage, Long gid) throws StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????????????????");
        }

        if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        return groupContestEntityService.getContestList(limit, currentPage, gid);
    }

    public IPage<Contest> getAdminContestList(Integer limit, Integer currentPage, Long gid) throws StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????????????????");
        }

        if (!isRoot && !groupValidator.isGroupAdmin(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        return groupContestEntityService.getAdminContestList(limit, currentPage, gid);
    }

    public AdminContestVO getContest(Long cid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = contest.getGid();

        if (gid == null){
            throw new StatusForbiddenException("?????????????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("?????????????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUid().equals(contest.getUid()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        AdminContestVO adminContestVo = BeanUtil.copyProperties(contest, AdminContestVO.class, "starAccount");
        if (StringUtils.isEmpty(contest.getStarAccount())) {
            adminContestVo.setStarAccount(new ArrayList<>());
        } else {
            try {
                JSONObject jsonObject = JSONUtil.parseObj(contest.getStarAccount());
                List<String> starAccount = jsonObject.get("star_account", List.class);
                adminContestVo.setStarAccount(starAccount);
            } catch (Exception e) {
                adminContestVo.setStarAccount(new ArrayList<>());
            }
        }

        if (contest.getAwardType() != null && contest.getAwardType() != 0) {
            try {
                JSONObject jsonObject = JSONUtil.parseObj(contest.getAwardConfig());
                List<ContestAwardConfigVO> awardConfigList = jsonObject.get("config", List.class);
                adminContestVo.setAwardConfigList(awardConfigList);
            } catch (Exception e) {
                adminContestVo.setAwardConfigList(new ArrayList<>());
            }
        } else {
            adminContestVo.setAwardConfigList(new ArrayList<>());
        }

        return adminContestVo;
    }

    public void addContest(AdminContestVO adminContestVo) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {

        contestValidator.validateContest(adminContestVo);
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long gid = adminContestVo.getGid();

        if (gid == null){
            throw new StatusNotFoundException("????????????????????????????????????ID???????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!isRoot && !groupValidator.isGroupAdmin(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        Contest contest = BeanUtil.copyProperties(adminContestVo, Contest.class, "starAccount");
        JSONObject accountJson = new JSONObject();
        if (adminContestVo.getStarAccount() == null) {
            accountJson.set("star_account", new ArrayList<>());
        } else {
            accountJson.set("star_account", adminContestVo.getStarAccount());
        }
        contest.setStarAccount(accountJson.toString());

        if (adminContestVo.getAwardType() != null && adminContestVo.getAwardType() != 0) {
            JSONObject awardConfigJson = new JSONObject();
            List<ContestAwardConfigVO> awardConfigList = adminContestVo.getAwardConfigList();
            awardConfigList.sort(Comparator.comparingInt(ContestAwardConfigVO::getPriority));
            awardConfigJson.set("config", awardConfigList);
            contest.setAwardConfig(awardConfigJson.toString());
        }

        contest.setIsGroup(true);

        boolean isOk = contestEntityService.save(contest);
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }

    public void updateContest(AdminContestVO adminContestVo) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {

        contestValidator.validateContest(adminContestVo);

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long cid = adminContestVo.getId();

        if (cid == null){
            throw new StatusNotFoundException("?????????????????????ID???????????????");
        }

        Contest oldContest = contestEntityService.getById(cid);

        if (oldContest == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = oldContest.getGid();

        if (gid == null){
            throw new StatusForbiddenException("???????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUid().equals(oldContest.getUid()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        Contest contest = BeanUtil.copyProperties(adminContestVo, Contest.class, "starAccount");
        JSONObject accountJson = new JSONObject();
        accountJson.set("star_account", adminContestVo.getStarAccount());
        contest.setStarAccount(accountJson.toString());

        if (adminContestVo.getAwardType() != null && adminContestVo.getAwardType() != 0) {
            List<ContestAwardConfigVO> awardConfigList = adminContestVo.getAwardConfigList();
            awardConfigList.sort(Comparator.comparingInt(ContestAwardConfigVO::getPriority));
            JSONObject awardConfigJson = new JSONObject();
            awardConfigJson.set("config", awardConfigList);
            contest.setAwardConfig(awardConfigJson.toString());
        }

        boolean isOk = contestEntityService.saveOrUpdate(contest);
        if (isOk) {
            if (!contest.getAuth().equals(Constants.Contest.AUTH_PUBLIC.getCode())) {
                if (!Objects.equals(oldContest.getPwd(), contest.getPwd())) {
                    UpdateWrapper<ContestRegister> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("cid", contest.getId());
                    contestRegisterEntityService.remove(updateWrapper);
                }
            }
        } else {
            throw new StatusFailException("????????????");
        }
    }

    public void deleteContest(Long cid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = contest.getGid();

        if (gid == null){
            throw new StatusForbiddenException("???????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUid().equals(contest.getUid()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = contestEntityService.removeById(cid);
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }

    public void changeContestVisible(Long cid, Boolean visible) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = contest.getGid();

        if (gid == null){
            throw new StatusForbiddenException("???????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUid().equals(contest.getUid()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        UpdateWrapper<Contest> contestUpdateWrapper = new UpdateWrapper<>();
        contestUpdateWrapper.eq("id", cid).set("visible", visible);

        boolean isOK = contestEntityService.update(contestUpdateWrapper);
        if (!isOK) {
            throw new StatusFailException("????????????");
        }
    }
}
