package cn.edu.hqu.databackup.manager.oj;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.common.exception.StatusAccessDeniedException;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.dao.group.GroupMemberEntityService;
import cn.edu.hqu.databackup.dao.judge.JudgeEntityService;
import cn.edu.hqu.databackup.dao.training.*;
import cn.edu.hqu.databackup.dao.user.UserInfoEntityService;
import cn.edu.hqu.databackup.manager.admin.training.AdminTrainingRecordManager;
import cn.edu.hqu.databackup.pojo.bo.Pair_;
import cn.edu.hqu.databackup.pojo.dto.RegisterTrainingDTO;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.api.pojo.entity.training.*;
import cn.edu.hqu.databackup.pojo.vo.*;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.validator.GroupValidator;
import cn.edu.hqu.databackup.validator.TrainingValidator;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Component
public class TrainingManager {

    @Resource
    private TrainingEntityService trainingEntityService;

    @Resource
    private TrainingRegisterEntityService trainingRegisterEntityService;

    @Resource
    private TrainingCategoryEntityService trainingCategoryEntityService;

    @Resource
    private TrainingProblemEntityService trainingProblemEntityService;

    @Resource
    private TrainingRecordEntityService trainingRecordEntityService;

    @Resource
    private UserInfoEntityService userInfoEntityService;

    @Resource
    private AdminTrainingRecordManager adminTrainingRecordManager;

    @Resource
    private GroupMemberEntityService groupMemberEntityService;

    @Resource
    private JudgeEntityService judgeEntityService;

    @Autowired
    private GroupValidator groupValidator;

    @Resource
    private TrainingValidator trainingValidator;

    /**
     * @param limit
     * @param currentPage
     * @param keyword
     * @param categoryId
     * @param auth
     * @MethodName getTrainingList
     * @Description ??????????????????????????????????????????????????????????????????????????????
     * @Return
     */
    public IPage<TrainingVO> getTrainingList(Integer limit,
                                             Integer currentPage,
                                             String keyword,
                                             Long categoryId,
                                             String auth) {

        // ????????????????????????????????????????????????
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 20;

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        String currentUid = null;
        if (userRolesVo != null) {
            currentUid = userRolesVo.getUid();
        }

        return trainingEntityService.getTrainingList(limit, currentPage, categoryId, auth, keyword, currentUid);
    }


    /**
     * @param tid
     * @MethodName getTraining
     * @Description ??????tid????????????????????????
     * @Return
     */
    public TrainingVO getTraining(Long tid) throws StatusFailException, StatusAccessDeniedException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("???????????????????????????????????????");
        }

        Long gid = training.getGid();
        if (training.getIsGroup()) {
            if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), training.getGid())) {
                throw new StatusForbiddenException("?????????????????????????????????");
            }
        } else {
            gid = null;
        }

        TrainingVO trainingVo = BeanUtil.copyProperties(training, TrainingVO.class);
        TrainingCategory trainingCategory = trainingCategoryEntityService.getTrainingCategoryByTrainingId(training.getId());
        trainingVo.setCategoryName(trainingCategory.getName());
        trainingVo.setCategoryColor(trainingCategory.getColor());
        List<Long> trainingProblemIdList = trainingProblemEntityService.getTrainingProblemIdList(training.getId());
        trainingVo.setProblemCount(trainingProblemIdList.size());

        if (userRolesVo != null && trainingValidator.isInTrainingOrAdmin(training, userRolesVo)) {
            Integer count = trainingProblemEntityService.getUserTrainingACProblemCount(userRolesVo.getUid(), gid, trainingProblemIdList);
            trainingVo.setAcCount(count);
        } else {
            trainingVo.setAcCount(0);
        }

        return trainingVo;
    }

    /**
     * @param tid
     * @MethodName getTrainingProblemList
     * @Description ??????tid???????????????????????????????????????
     * @Return
     */
    public List<ProblemVO> getTrainingProblemList(Long tid) throws StatusAccessDeniedException,
            StatusForbiddenException, StatusFailException {
        Training training = trainingEntityService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("???????????????????????????????????????");
        }
        trainingValidator.validateTrainingAuth(training);

        return trainingProblemEntityService.getTrainingProblemList(tid);
    }

    /**
     * @param registerTrainingDto
     * @MethodName toRegisterTraining
     * @Description ?????????????????????????????????
     * @Return
     */
    public void toRegisterTraining(RegisterTrainingDTO registerTrainingDto) throws StatusFailException, StatusForbiddenException {

        Long tid = registerTrainingDto.getTid();
        String password = registerTrainingDto.getPassword();

        if (tid == null || StringUtils.isEmpty(password)) {
            throw new StatusFailException("???????????????????????????");
        }

        Training training = trainingEntityService.getById(tid);

        if (training == null || !training.getStatus()) {
            throw new StatusFailException("????????????????????????????????????????????????!");
        }

        if (!training.getPrivatePwd().equals(password)) { // ????????????
            throw new StatusFailException("???????????????????????????????????????");
        }

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        QueryWrapper<TrainingRegister> registerQueryWrapper = new QueryWrapper<>();
        registerQueryWrapper.eq("tid", tid).eq("uid", userRolesVo.getUid());
        if (trainingRegisterEntityService.count(registerQueryWrapper) > 0) {
            throw new StatusFailException("????????????????????????????????????????????????");
        }

        boolean isOk = trainingRegisterEntityService.save(new TrainingRegister()
                .setTid(tid)
                .setUid(userRolesVo.getUid()));

        if (!isOk) {
            throw new StatusFailException("??????????????????????????????????????????");
        } else {
            adminTrainingRecordManager.syncUserSubmissionToRecordByTid(tid, userRolesVo.getUid());
        }
    }


    /**
     * @param tid
     * @MethodName getTrainingAccess
     * @Description ???????????????????????????????????????????????????????????????????????????
     * @Return
     */
    public AccessVO getTrainingAccess(Long tid) throws StatusFailException {

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        QueryWrapper<TrainingRegister> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tid", tid).eq("uid", userRolesVo.getUid());
        TrainingRegister trainingRegister = trainingRegisterEntityService.getOne(queryWrapper, false);
        boolean access = false;
        if (trainingRegister != null) {
            access = true;
            Training training = trainingEntityService.getById(tid);
            if (training == null || !training.getStatus()) {
                throw new StatusFailException("??????????????????????????????!");
            }
        }

        AccessVO accessVo = new AccessVO();
        accessVo.setAccess(access);

        return accessVo;
    }


    /**
     * @param tid
     * @param limit
     * @param currentPage
     * @param keyword     ??????????????? ??????????????????????????????????????????
     * @MethodName getTrainingRnk
     * @Description ??????????????????????????????
     * @Return
     */
    public IPage<TrainingRankVO> getTrainingRank(Long tid, Integer limit, Integer currentPage, String keyword) throws
            StatusAccessDeniedException, StatusForbiddenException, StatusFailException {

        Training training = trainingEntityService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("???????????????????????????????????????");
        }

        trainingValidator.validateTrainingAuth(training);

        // ?????????????????????????????????????????????
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 30;

        if (StrUtil.isNotBlank(keyword)) {
            keyword = keyword.toLowerCase();
        }
        return getTrainingRank(tid, training.getIsGroup() ? training.getGid() : null,
                training.getAuthor(),
                currentPage,
                limit,
                keyword);
    }

    private IPage<TrainingRankVO> getTrainingRank(Long tid, Long gid, String username, int currentPage, int limit, String keyword) {

        Map<Long, String> tpIdMapDisplayId = getTPIdMapDisplayId(tid);
        List<TrainingRecordVO> trainingRecordVOList = trainingRecordEntityService.getTrainingRecord(tid);

        List<String> superAdminUidList = userInfoEntityService.getSuperAdminUidList();
        if (gid != null) {
            List<String> groupRootUidList = groupMemberEntityService.getGroupRootUidList(gid);
            superAdminUidList.addAll(groupRootUidList);
        }

        List<TrainingRankVO> result = new ArrayList<>();

        HashMap<String, Integer> uidMapIndex = new HashMap<>();
        int pos = 0;
        for (TrainingRecordVO trainingRecordVo : trainingRecordVOList) {
            // ?????????????????????????????????????????????????????????
            if (username.equals(trainingRecordVo.getUsername())
                    || superAdminUidList.contains(trainingRecordVo.getUid())) {
                continue;
            }

            // ??????????????????????????? ???????????????????????? ???????????????????????????????????????????????????????????????
            if (StrUtil.isNotBlank(keyword)) {
                boolean isMatchKeyword = matchKeywordIgnoreCase(keyword, trainingRecordVo.getUsername())
                        || matchKeywordIgnoreCase(keyword, trainingRecordVo.getRealname())
                        || matchKeywordIgnoreCase(keyword, trainingRecordVo.getSchool());
                if (!isMatchKeyword) {
                    continue;
                }
            }

            TrainingRankVO trainingRankVo;
            Integer index = uidMapIndex.get(trainingRecordVo.getUid());
            if (index == null) {
                trainingRankVo = new TrainingRankVO();
                trainingRankVo.setRealname(trainingRecordVo.getRealname())
                        .setAvatar(trainingRecordVo.getAvatar())
                        .setSchool(trainingRecordVo.getSchool())
                        .setGender(trainingRecordVo.getGender())
                        .setUid(trainingRecordVo.getUid())
                        .setUsername(trainingRecordVo.getUsername())
                        .setNickname(trainingRecordVo.getNickname())
                        .setAc(0)
                        .setTotalRunTime(0);
                HashMap<String, HashMap<String, Object>> submissionInfo = new HashMap<>();
                trainingRankVo.setSubmissionInfo(submissionInfo);

                result.add(trainingRankVo);
                uidMapIndex.put(trainingRecordVo.getUid(), pos);
                pos++;
            } else {
                trainingRankVo = result.get(index);
            }
            String displayId = tpIdMapDisplayId.get(trainingRecordVo.getTpid());
            HashMap<String, Object> problemSubmissionInfo = trainingRankVo
                    .getSubmissionInfo()
                    .getOrDefault(displayId, new HashMap<>());

            // ?????????????????????AC???????????????????????????????????????
            if ((Boolean) problemSubmissionInfo.getOrDefault("isAC", false)) {
                if (trainingRecordVo.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
                    int runTime = (int) problemSubmissionInfo.getOrDefault("runTime", 0);
                    if (runTime > trainingRecordVo.getUseTime()) {
                        trainingRankVo.setTotalRunTime(trainingRankVo.getTotalRunTime() - runTime + trainingRecordVo.getUseTime());
                        problemSubmissionInfo.put("runTime", trainingRecordVo.getUseTime());
                    }
                }
                continue;
            }

            problemSubmissionInfo.put("status", trainingRecordVo.getStatus());
            problemSubmissionInfo.put("score", trainingRecordVo.getScore());

            // ????????????
            if (trainingRecordVo.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
                // ?????????????????????ac+1
                trainingRankVo.setAc(trainingRankVo.getAc() + 1);
                problemSubmissionInfo.put("isAC", true);
                problemSubmissionInfo.put("runTime", trainingRecordVo.getUseTime());
                trainingRankVo.setTotalRunTime(trainingRankVo.getTotalRunTime() + trainingRecordVo.getUseTime());
            }

            trainingRankVo.getSubmissionInfo().put(displayId, problemSubmissionInfo);
        }

        List<TrainingRankVO> orderResultList = result.stream().sorted(Comparator.comparing(TrainingRankVO::getAc, Comparator.reverseOrder()) // ?????????ac?????????
                .thenComparing(TrainingRankVO::getTotalRunTime) //?????????????????????
        ).collect(Collectors.toList());

        // ???????????????????????????????????????
        Page<TrainingRankVO> page = new Page<>(currentPage, limit);
        int count = orderResultList.size();
        List<TrainingRankVO> pageList = new ArrayList<>();
        //???????????????????????????????????????
        int currId = currentPage > 1 ? (currentPage - 1) * limit : 0;
        for (int i = 0; i < limit && i < count - currId; i++) {
            pageList.add(orderResultList.get(currId + i));
        }
        page.setSize(limit);
        page.setCurrent(currentPage);
        page.setTotal(count);
        page.setRecords(pageList);
        return page;
    }

    private boolean matchKeywordIgnoreCase(String keyword, String content) {
        return content != null && content.toLowerCase().contains(keyword);
    }

    private Map<Long, String> getTPIdMapDisplayId(Long tid) {
        QueryWrapper<TrainingProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tid", tid);
        List<TrainingProblem> trainingProblemList = trainingProblemEntityService.list(queryWrapper);
        return trainingProblemList.stream().collect(Collectors.toMap(TrainingProblem::getId, TrainingProblem::getDisplayId));
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    @Async
    public void checkAndSyncTrainingRecord(Long pid, Long submitId, String uid) {
        List<TrainingProblem> trainingProblemList = trainingProblemEntityService.getPrivateTrainingProblemListByPid(pid, uid);
        if (!CollectionUtils.isEmpty(trainingProblemList)) {
            List<TrainingRecord> trainingRecordList = new ArrayList<>();
            for (TrainingProblem trainingProblem : trainingProblemList) {
                TrainingRecord trainingRecord = new TrainingRecord();
                trainingRecord.setPid(pid)
                        .setTid(trainingProblem.getTid())
                        .setTpid(trainingProblem.getId())
                        .setSubmitId(submitId)
                        .setUid(uid);
                trainingRecordList.add(trainingRecord);
            }
            trainingRecordEntityService.saveBatch(trainingRecordList);
        }
    }

    public List<ProblemFullScreenListVO> getProblemFullScreenList(Long tid)
            throws StatusFailException, StatusForbiddenException, StatusAccessDeniedException {
        Training training = trainingEntityService.getById(tid);
        if (training == null || !training.getStatus()) {
            throw new StatusFailException("???????????????????????????????????????");
        }
        trainingValidator.validateTrainingAuth(training);
        List<ProblemFullScreenListVO> problemList = trainingProblemEntityService.getTrainingFullScreenProblemList(tid);

        List<Long> pidList = problemList.stream().map(ProblemFullScreenListVO::getPid).collect(Collectors.toList());
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct pid,status,score,submit_time")
                .in("pid", pidList)
                .eq("uid", userRolesVo.getUid())
                .orderByDesc("submit_time");

        queryWrapper.eq("cid", 0);
        if (training.getGid() != null && training.getIsGroup()) {
            queryWrapper.eq("gid", training.getGid());
        } else {
            queryWrapper.isNull("gid");
        }
        List<Judge> judges = judgeEntityService.list(queryWrapper);
        HashMap<Long, Pair_<Integer, Integer>> pidMap = new HashMap<>();
        for (Judge judge : judges) {
            if (Objects.equals(judge.getStatus(), Constants.Judge.STATUS_PENDING.getStatus())
                    || Objects.equals(judge.getStatus(), Constants.Judge.STATUS_COMPILING.getStatus())
                    || Objects.equals(judge.getStatus(), Constants.Judge.STATUS_JUDGING.getStatus())) {
                continue;
            }
            if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
                // ???????????????????????????????????????????????????0???
                pidMap.put(judge.getPid(), new Pair_<>(judge.getStatus(), judge.getScore()));
            } else if (!pidMap.containsKey(judge.getPid())) {
                // ???????????????????????????????????????????????????
                pidMap.put(judge.getPid(), new Pair_<>(judge.getStatus(), judge.getScore()));
            }
        }
        for (ProblemFullScreenListVO problemVO : problemList) {
            Pair_<Integer, Integer> pair_ = pidMap.get(problemVO.getPid());
            if (pair_ != null) {
                problemVO.setStatus(pair_.getKey());
                problemVO.setScore(pair_.getValue());
            }
        }
        return problemList;
    }
}