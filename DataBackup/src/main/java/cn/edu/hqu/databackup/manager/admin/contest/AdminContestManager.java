package cn.edu.hqu.databackup.manager.admin.contest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusSystemErrorException;
import cn.edu.hqu.databackup.dao.contest.ContestEntityService;
import cn.edu.hqu.databackup.dao.contest.ContestRegisterEntityService;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.contest.ContestRegister;
import cn.edu.hqu.databackup.pojo.vo.AdminContestVO;
import cn.edu.hqu.databackup.pojo.vo.ContestAwardConfigVO;
import cn.edu.hqu.databackup.pojo.vo.UserRolesVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.validator.ContestValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @Author: egret
 */
@Component
@Slf4j(topic = "hoj")
public class AdminContestManager {

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private ContestRegisterEntityService contestRegisterEntityService;

    @Autowired
    private ContestValidator contestValidator;

    public IPage<Contest> getContestList(Integer limit, Integer currentPage, String keyword) {

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        IPage<Contest> iPage = new Page<>(currentPage, limit);
        QueryWrapper<Contest> queryWrapper = new QueryWrapper<>();
        // ????????????
        queryWrapper.select(Contest.class, info -> !info.getColumn().equals("pwd"));
        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
            queryWrapper
                    .like("title", keyword).or()
                    .like("id", keyword);
        }
        queryWrapper.eq("is_group", false).orderByDesc("start_time");
        return contestEntityService.page(iPage, queryWrapper);
    }

    public AdminContestVO getContest(Long cid) throws StatusFailException, StatusForbiddenException {
        Contest contest = contestEntityService.getById(cid);
        if (contest == null) { // ???????????????
            throw new StatusFailException("?????????????????????????????????,???????????????cid???????????????");
        }
        // ???????????????????????????
        UserRolesVO userRolesVo = (UserRolesVO) SecurityUtils.getSubject().getSession().getAttribute("userInfo");

        // ????????????????????????
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        // ???????????????????????????????????????????????????
        if (!isRoot && !userRolesVo.getUid().equals(contest.getUid())) {
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

    public void deleteContest(Long cid) throws StatusFailException {
        boolean isOk = contestEntityService.removeById(cid);
        /*
        contest???id?????????????????????????????????????????????????????????????????????
         */
        if (!isOk) { // ????????????
            throw new StatusFailException("????????????");
        }
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        log.info("[{}],[{}],cid:[{}],operatorUid:[{}],operatorUsername:[{}]",
                "Admin_Contest", "Delete", cid, userRolesVo.getUid(), userRolesVo.getUsername());
    }

    public void addContest(AdminContestVO adminContestVo) throws StatusFailException {
        contestValidator.validateContest(adminContestVo);

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

        boolean isOk = contestEntityService.save(contest);
        if (!isOk) { // ????????????
            throw new StatusFailException("????????????");
        }
    }

    public void cloneContest(Long cid) throws StatusSystemErrorException {
        Contest contest = contestEntityService.getById(cid);
        if (contest == null) {
            throw new StatusSystemErrorException("????????????????????????????????????");
        }
        // ???????????????????????????
        UserRolesVO userRolesVo = (UserRolesVO) SecurityUtils.getSubject().getSession().getAttribute("userInfo");
        contest.setUid(userRolesVo.getUid())
                .setAuthor(userRolesVo.getUsername())
                .setSource(cid.intValue())
                .setId(null)
                .setGmtCreate(null)
                .setGmtModified(null);
        contest.setTitle(contest.getTitle() + " [Cloned]");
        contestEntityService.save(contest);
    }

    public void updateContest(AdminContestVO adminContestVo) throws StatusForbiddenException, StatusFailException {
        contestValidator.validateContest(adminContestVo);

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        // ????????????????????????
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        // ???????????????????????????????????????????????????
        if (!isRoot && !userRolesVo.getUid().equals(adminContestVo.getUid())) {
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


        Contest oldContest = contestEntityService.getById(contest.getId());
        boolean isOk = contestEntityService.saveOrUpdate(contest);
        if (isOk) {
            if (!contest.getAuth().equals(Constants.Contest.AUTH_PUBLIC.getCode())) {
                if (!Objects.equals(oldContest.getPwd(), contest.getPwd())) { // ????????????????????????????????????????????????????????????
                    UpdateWrapper<ContestRegister> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("cid", contest.getId());
                    contestRegisterEntityService.remove(updateWrapper);
                }
            }
        } else {
            throw new StatusFailException("????????????");
        }
    }

    public void changeContestVisible(Long cid, String uid, Boolean visible) throws StatusFailException, StatusForbiddenException {
        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        // ????????????????????????
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        // ???????????????????????????????????????????????????
        if (!isRoot && !userRolesVo.getUid().equals(uid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOK = contestEntityService.saveOrUpdate(new Contest().setId(cid).setVisible(visible));

        if (!isOK) {
            throw new StatusFailException("????????????");
        }
        log.info("[{}],[{}],value:[{}],cid:[{}],operatorUid:[{}],operatorUsername:[{}]",
                "Admin_Contest", "Change_Visible", visible, cid, userRolesVo.getUid(), userRolesVo.getUsername());
    }

}