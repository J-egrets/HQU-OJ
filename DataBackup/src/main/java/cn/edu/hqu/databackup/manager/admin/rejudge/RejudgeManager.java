package cn.edu.hqu.databackup.manager.admin.rejudge;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.dao.contest.ContestRecordEntityService;
import cn.edu.hqu.databackup.dao.judge.JudgeCaseEntityService;
import cn.edu.hqu.databackup.dao.judge.JudgeEntityService;
import cn.edu.hqu.databackup.dao.problem.ProblemEntityService;
import cn.edu.hqu.databackup.dao.user.UserAcproblemEntityService;
import cn.edu.hqu.databackup.judge.remote.RemoteJudgeDispatcher;
import cn.edu.hqu.databackup.judge.self.JudgeDispatcher;
import cn.edu.hqu.api.pojo.entity.contest.ContestRecord;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.api.pojo.entity.judge.JudgeCase;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.api.pojo.entity.user.UserAcproblem;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: egret
 */
@Component
public class RejudgeManager {

    @Resource
    private JudgeEntityService judgeEntityService;

    @Resource
    private UserAcproblemEntityService userAcproblemEntityService;

    @Resource
    private ContestRecordEntityService contestRecordEntityService;

    @Resource
    private JudgeCaseEntityService judgeCaseEntityService;

    @Resource
    private ProblemEntityService problemEntityService;

    @Resource
    private JudgeDispatcher judgeDispatcher;

    @Resource
    private RemoteJudgeDispatcher remoteJudgeDispatcher;

    private static List<Integer> penaltyStatus = Arrays.asList(
            Constants.Judge.STATUS_PRESENTATION_ERROR.getStatus(),
            Constants.Judge.STATUS_WRONG_ANSWER.getStatus(),
            Constants.Judge.STATUS_TIME_LIMIT_EXCEEDED.getStatus(),
            Constants.Judge.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus(),
            Constants.Judge.STATUS_RUNTIME_ERROR.getStatus());


    public Judge rejudge(Long submitId) throws StatusFailException {
        Judge judge = judgeEntityService.getById(submitId);

        boolean isContestSubmission = judge.getCid() != 0;

        boolean hasSubmitIdRemoteRejudge = checkAndUpdateJudge(isContestSubmission, judge, submitId);
        // ??????????????????
        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.select("id", "is_remote", "problem_id")
                .eq("id", judge.getPid());
        Problem problem = problemEntityService.getOne(problemQueryWrapper);
        if (problem.getIsRemote()) { // ???????????????oj??????
            remoteJudgeDispatcher.sendTask(judge.getSubmitId(), judge.getPid(), problem.getProblemId(),
                    isContestSubmission, hasSubmitIdRemoteRejudge);
        } else {
            judgeDispatcher.sendTask(judge.getSubmitId(), judge.getPid(), isContestSubmission);
        }
        return judge;
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejudgeContestProblem(Long cid, Long pid) throws StatusFailException {
        QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid).eq("pid", pid);
        List<Judge> rejudgeList = judgeEntityService.list(queryWrapper);

        if (rejudgeList.size() == 0) {
            throw new StatusFailException("??????????????????????????????????????????");
        }
        HashMap<Long, Integer> idMapStatus = new HashMap<>();
        // ?????????????????????
        checkAndUpdateJudgeBatch(rejudgeList, idMapStatus);
        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.select("id", "is_remote", "problem_id")
                .eq("id", pid);
        Problem problem = problemEntityService.getOne(problemQueryWrapper);
        // ??????????????????
        if (problem.getIsRemote()) { // ???????????????oj??????
            for (Judge judge : rejudgeList) {
                // ?????????????????????????????????????????????
                remoteJudgeDispatcher.sendTask(judge.getSubmitId(),
                        pid,
                        problem.getProblemId(),
                        judge.getCid() != 0,
                        isHasSubmitIdRemoteRejudge(judge.getVjudgeSubmitId(), idMapStatus.get(judge.getSubmitId())));
            }
        } else {
            for (Judge judge : rejudgeList) {
                // ?????????????????????????????????????????????
                judgeDispatcher.sendTask(judge.getSubmitId(), judge.getPid(), judge.getCid() != 0);
            }
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public boolean checkAndUpdateJudge(Boolean isContestSubmission, Judge judge, Long submitId) throws StatusFailException {
        // ????????????????????????
        boolean resetContestRecordResult = true;
        if (!isContestSubmission) {
            // ?????????????????????????????????????????????????????????
            // ?????????????????????AC???????????????????????????????????????ac????????? user_acproblem
            if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus().intValue()) {
                QueryWrapper<UserAcproblem> userAcproblemQueryWrapper = new QueryWrapper<>();
                userAcproblemQueryWrapper.eq("submit_id", judge.getSubmitId());
                userAcproblemEntityService.remove(userAcproblemQueryWrapper);
            }
        } else {
            // ???????????????????????????????????????
            UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("submit_id", submitId).setSql("status=null,score=null");
            resetContestRecordResult = contestRecordEntityService.update(updateWrapper);
        }

        // ???????????????????????????????????????
        QueryWrapper<JudgeCase> judgeCaseQueryWrapper = new QueryWrapper<>();
        judgeCaseQueryWrapper.eq("submit_id", submitId);
        judgeCaseEntityService.remove(judgeCaseQueryWrapper);

        boolean hasSubmitIdRemoteRejudge = isHasSubmitIdRemoteRejudge(judge.getVjudgeSubmitId(), judge.getStatus());

        // ???????????????
        judge.setStatus(Constants.Judge.STATUS_PENDING.getStatus()); // ????????????????????????
        judge.setVersion(judge.getVersion() + 1);
        judge.setJudger("")
                .setIsManual(false)
                .setTime(null)
                .setMemory(null)
                .setErrorMessage(null)
                .setOiRankScore(null)
                .setScore(null);
        boolean isUpdateJudgeOk = judgeEntityService.updateById(judge);

        if (!resetContestRecordResult || !isUpdateJudgeOk) {
            throw new StatusFailException("?????????????????????????????????");
        }
        return hasSubmitIdRemoteRejudge;
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkAndUpdateJudgeBatch(List<Judge> rejudgeList, HashMap<Long, Integer> idMapStatus) throws StatusFailException {
        List<Long> submitIdList = new LinkedList<>();
        // ?????????????????????
        for (Judge judge : rejudgeList) {
            idMapStatus.put(judge.getSubmitId(), judge.getStatus());
            judge.setStatus(Constants.Judge.STATUS_PENDING.getStatus()); // ????????????????????????
            judge.setVersion(judge.getVersion() + 1);
            judge.setJudger("")
                    .setTime(null)
                    .setMemory(null)
                    .setErrorMessage(null)
                    .setOiRankScore(null)
                    .setIsManual(false)
                    .setScore(null);
            submitIdList.add(judge.getSubmitId());
        }
        boolean resetJudgeResult = judgeEntityService.updateBatchById(rejudgeList);
        // ??????????????????????????????????????????
        QueryWrapper<JudgeCase> judgeCaseQueryWrapper = new QueryWrapper<>();
        judgeCaseQueryWrapper.in("submit_id", submitIdList);
        judgeCaseEntityService.remove(judgeCaseQueryWrapper);
        // ???????????????????????????????????????
        UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("submit_id", submitIdList).setSql("status=null,score=null");
        boolean resetContestRecordResult = contestRecordEntityService.update(updateWrapper);

        if (!resetContestRecordResult || !resetJudgeResult) {
            throw new StatusFailException("?????????????????????????????????");
        }
    }

    private boolean isHasSubmitIdRemoteRejudge(Long vjudgeSubmitId, int status) {
        boolean isHasSubmitIdRemoteRejudge = false;
        if (vjudgeSubmitId != null &&
                (status == Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus()
                        || status == Constants.Judge.STATUS_COMPILING.getStatus()
                        || status == Constants.Judge.STATUS_PENDING.getStatus()
                        || status == Constants.Judge.STATUS_JUDGING.getStatus()
                        || status == Constants.Judge.STATUS_SYSTEM_ERROR.getStatus())) {
            isHasSubmitIdRemoteRejudge = true;
        }
        return isHasSubmitIdRemoteRejudge;
    }

    @Transactional(rollbackFor = Exception.class)
    public Judge manualJudge(Long submitId, Integer status, Integer score) throws StatusFailException {
        QueryWrapper<Judge> judgeQueryWrapper = new QueryWrapper<>();
        judgeQueryWrapper
                .select("submit_id", "status", "judger", "cid", "pid", "uid")
                .eq("submit_id", submitId);
        Judge judge = judgeEntityService.getOne(judgeQueryWrapper);
        if (judge == null) {
            throw new StatusFailException("???????????????????????????????????????");
        }
        if (judge.getStatus().equals(Constants.Judge.STATUS_JUDGING.getStatus())
                || judge.getStatus().equals(Constants.Judge.STATUS_COMPILING.getStatus())
                || judge.getStatus().equals(Constants.Judge.STATUS_PENDING.getStatus())) {
            throw new StatusFailException("????????????????????????????????????????????????????????????????????????");
        }
        if (judge.getStatus().equals(Constants.Judge.STATUS_COMPILE_ERROR.getStatus())) {
            throw new StatusFailException("?????????????????????????????????????????????");
        }
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
        judgeUpdateWrapper
                .set("status", status)
                .set("is_manual", true)
                .set("judger", userRolesVo.getUsername())
                .eq("submit_id", judge.getSubmitId());
        Integer oiRankScore = null;
        if (score != null) {
            Problem problem = problemEntityService.getById(judge.getPid());
            if (problem != null && Objects.equals(problem.getType(), Constants.Contest.TYPE_OI.getCode())
                    && problem.getIoScore() != null) {
                if (score > problem.getIoScore()) {
                    score = problem.getIoScore();
                } else if (score < 0) {
                    score = 0;
                }
                oiRankScore = (int) Math.round(problem.getDifficulty() * 2 + 0.1 * score);
                judgeUpdateWrapper.set("score", score)
                        .set("oi_rank_score", oiRankScore);
            } else {
                score = null;
            }
        }

        boolean isUpdateOK = judgeEntityService.update(judgeUpdateWrapper);
        if (!isUpdateOK) {
            throw new StatusFailException("????????????????????????????????????????????????????????????????????????");
        }

        // ????????????AC,???????????????????????????AC,?????????user_acproblem??????????????????
        if (Objects.equals(judge.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus())
                && !Objects.equals(status, Constants.Judge.STATUS_ACCEPTED.getStatus())) {
            QueryWrapper<UserAcproblem> userAcproblemQueryWrapper = new QueryWrapper<>();
            userAcproblemQueryWrapper.eq("submit_id", judge.getSubmitId());
            userAcproblemEntityService.remove(userAcproblemQueryWrapper);
        } else if (!Objects.equals(judge.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus())
                && Objects.equals(status, Constants.Judge.STATUS_ACCEPTED.getStatus())) {
            // ??????????????????AC,????????????????????????AC,?????????user_acproblem???
            if (status.intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus() && judge.getGid() == null) {
                userAcproblemEntityService.saveOrUpdate(new UserAcproblem()
                        .setPid(judge.getPid())
                        .setUid(judge.getUid())
                        .setSubmitId(submitId)
                );
            }
        }

        if (judge.getCid() != 0) {
            UpdateWrapper<ContestRecord> contestRecordUpdateWrapper = new UpdateWrapper<>();
            contestRecordUpdateWrapper.eq("submit_id", submitId)
                    .eq("cid", judge.getCid());
            if (Objects.equals(status, Constants.Judge.STATUS_ACCEPTED.getStatus())) {
                contestRecordUpdateWrapper.set("status", Constants.Contest.RECORD_AC.getCode());
            } else if (penaltyStatus.contains(status)) {
                contestRecordUpdateWrapper.set("status", Constants.Contest.RECORD_NOT_AC_PENALTY.getCode());
            } else {
                contestRecordUpdateWrapper.set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
            }
            contestRecordUpdateWrapper.set(score != null, "score", score);
            contestRecordEntityService.update(contestRecordUpdateWrapper);
        }
        Judge res = new Judge();
        res.setSubmitId(submitId)
                .setJudger(userRolesVo.getUsername())
                .setStatus(status)
                .setScore(score)
                .setOiRankScore(oiRankScore);
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    public Judge cancelJudge(Long submitId) throws StatusFailException {
        QueryWrapper<Judge> judgeQueryWrapper = new QueryWrapper<>();
        judgeQueryWrapper
                .select("submit_id", "status", "judger", "cid")
                .eq("submit_id", submitId)
                .last("for update");
        Judge judge = judgeEntityService.getOne(judgeQueryWrapper);
        if (judge == null) {
            throw new StatusFailException("???????????????????????????????????????");
        }
        if (judge.getStatus().equals(Constants.Judge.STATUS_JUDGING.getStatus())
                || judge.getStatus().equals(Constants.Judge.STATUS_COMPILING.getStatus())
                || (judge.getStatus().equals(Constants.Judge.STATUS_PENDING.getStatus())
                && !StringUtils.isEmpty(judge.getJudger()))) {
            throw new StatusFailException("????????????????????????????????????????????????????????????????????????");
        }
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
        judgeUpdateWrapper
                .setSql("status=-4,score=null,oi_rank_score=null,is_manual=true,judger='" + userRolesVo.getUsername() + "'")
                .eq("submit_id", judge.getSubmitId());
        boolean isUpdateOK = judgeEntityService.update(judgeUpdateWrapper);
        if (!isUpdateOK) {
            throw new StatusFailException("????????????????????????????????????????????????????????????????????????");
        }

        // ?????????????????????AC???????????????????????????????????????ac????????? user_acproblem
        if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus().intValue()) {
            QueryWrapper<UserAcproblem> userAcproblemQueryWrapper = new QueryWrapper<>();
            userAcproblemQueryWrapper.eq("submit_id", judge.getSubmitId());
            userAcproblemEntityService.remove(userAcproblemQueryWrapper);
        }

        if (judge.getCid() != 0) {
            UpdateWrapper<ContestRecord> contestRecordUpdateWrapper = new UpdateWrapper<>();
            contestRecordUpdateWrapper.eq("submit_id", submitId)
                    .eq("cid", judge.getCid())
                    .setSql("score=null")
                    .set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
            contestRecordEntityService.update(contestRecordUpdateWrapper);
        }
        Judge res = new Judge();
        res.setSubmitId(submitId)
                .setJudger(userRolesVo.getUsername())
                .setStatus(Constants.Judge.STATUS_CANCELLED.getStatus());
        return res;
    }
}