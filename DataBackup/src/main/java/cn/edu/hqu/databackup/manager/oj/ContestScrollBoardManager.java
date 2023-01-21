package cn.edu.hqu.databackup.manager.oj;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.dao.contest.ContestEntityService;
import cn.edu.hqu.databackup.dao.contest.ContestProblemEntityService;
import cn.edu.hqu.databackup.dao.judge.JudgeEntityService;
import cn.edu.hqu.databackup.dao.problem.ProblemEntityService;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.contest.ContestProblem;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.databackup.pojo.vo.ContestScrollBoardInfoVO;
import cn.edu.hqu.databackup.pojo.vo.ContestScrollBoardSubmissionVO;
import cn.edu.hqu.databackup.utils.Constants;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Component
public class ContestScrollBoardManager {

    @Resource
    private ContestEntityService contestEntityService;

    @Resource
    private ContestProblemEntityService contestProblemEntityService;

    @Resource
    private JudgeEntityService judgeEntityService;

    @Resource
    private ProblemEntityService problemEntityService;

    @Resource
    private ContestCalculateRankManager contestCalculateRankManager;


    public ContestScrollBoardInfoVO getContestScrollBoardInfo(Long cid) throws StatusFailException {
        Contest contest = contestEntityService.getById(cid);
        if (contest == null) {
            throw new StatusFailException("比赛不存在 (The contest does not exist)");
        }

        if (!Objects.equals(contest.getType(), Constants.Contest.TYPE_ACM.getCode())) {
            throw new StatusFailException("非ACM赛制的比赛无法进行滚榜  (Non - ACM contest board cannot be rolled)");
        }

        if (!contest.getSealRank()) {
            throw new StatusFailException("比赛未开启封榜，无法进行滚榜 (The contest has not been closed, and cannot roll)");
        }

        if (!Objects.equals(contest.getStatus(), Constants.Contest.STATUS_ENDED.getCode())) {
            throw new StatusFailException("比赛未结束，禁止进行滚榜 (Roll off is prohibited before the contest is over)");
        }

        QueryWrapper<ContestProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid);
        List<ContestProblem> contestProblemList = contestProblemEntityService.list(queryWrapper);

        List<Long> pidList = contestProblemList.stream().map(ContestProblem::getPid).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(pidList)) {
            QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.select("id", "auth")
                    .ne("auth", 2)
                    .in("id", pidList);
            List<Problem> problemList = problemEntityService.list(problemQueryWrapper);
            List<Long> idList = problemList.stream().map(Problem::getId).collect(Collectors.toList());
            contestProblemList = contestProblemList.stream()
                    .filter(p -> idList.contains(p.getPid()))
                    .collect(Collectors.toList());
        }

        HashMap<String, String> balloonColor = new HashMap<>();
        for (ContestProblem contestProblem : contestProblemList) {
            balloonColor.put(contestProblem.getDisplayId(), contestProblem.getColor());
        }

        ContestScrollBoardInfoVO info = new ContestScrollBoardInfoVO();
        info.setId(cid);
        info.setProblemCount(contestProblemList.size());
        info.setBalloonColor(balloonColor);
        info.setRankShowName(contest.getRankShowName());
        info.setStarUserList(starAccountToList(contest.getStarAccount()));
        info.setStartTime(contest.getStartTime());
        info.setSealRankTime(contest.getSealRankTime());

        return info;
    }

    private List<String> starAccountToList(String starAccountStr) {
        if (StringUtils.isEmpty(starAccountStr)) {
            return new ArrayList<>();
        }
        JSONObject jsonObject = JSONUtil.parseObj(starAccountStr);
        List<String> list = jsonObject.get("star_account", List.class);
        return list;
    }


    public List<ContestScrollBoardSubmissionVO> getContestScrollBoardSubmission(Long cid, Boolean removeStar) throws StatusFailException {
        Contest contest = contestEntityService.getById(cid);
        if (contest == null) {
            throw new StatusFailException("比赛不存在 (The contest does not exist)");
        }

        if (!Objects.equals(contest.getType(), Constants.Contest.TYPE_ACM.getCode())) {
            throw new StatusFailException("非ACM赛制的比赛无法进行滚榜  (Non - ACM contest board cannot be rolled)");
        }

        if (!contest.getSealRank()) {
            throw new StatusFailException("比赛未开启封榜，无法进行滚榜 (The contest has not been closed, and cannot roll)");
        }

        if (!Objects.equals(contest.getStatus(), Constants.Contest.STATUS_ENDED.getCode())) {
            throw new StatusFailException("比赛未结束，禁止进行滚榜 (Roll off is prohibited before the contest is over)");
        }

        List<String> removeUidList = contestCalculateRankManager.getSuperAdminUidList(contest.getGid());
        if (!removeUidList.contains(contest.getUid())) {
            removeUidList.add(contest.getUid());
        }
        List<ContestScrollBoardSubmissionVO> submissions = judgeEntityService.getContestScrollBoardSubmission(cid, removeUidList);
        if (removeStar && StrUtil.isNotBlank(contest.getStarAccount())) {
            JSONObject jsonObject = JSONUtil.parseObj(contest.getStarAccount());
            List<String> usernameList = jsonObject.get("star_account", List.class);
            if (!CollectionUtils.isEmpty(usernameList)) {
                submissions = submissions.stream()
                        .filter(submission -> !usernameList.contains(submission.getUsername()))
                        .collect(Collectors.toList());
            }
        }
        return submissions;
    }
}
