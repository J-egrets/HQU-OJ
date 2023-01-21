package cn.edu.hqu.databackup.judge.self;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import cn.edu.hqu.databackup.dao.contest.ContestRecordEntityService;
import cn.edu.hqu.databackup.dao.judge.JudgeEntityService;
import cn.edu.hqu.databackup.judge.AbstractReceiver;
import cn.edu.hqu.databackup.judge.Dispatcher;
import cn.edu.hqu.api.pojo.dto.TestJudgeReq;
import cn.edu.hqu.api.pojo.dto.ToJudgeDTO;
import cn.edu.hqu.api.pojo.entity.contest.ContestRecord;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.utils.RedisUtils;

import java.util.Objects;

/**
 * @Author: egret
 */
@Component
@Slf4j(topic = "hoj")
public class JudgeReceiver extends AbstractReceiver {

    @Autowired
    private Dispatcher dispatcher;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private ContestRecordEntityService contestRecordEntityService;

    @Async("judgeTaskAsyncPool")
    public void processWaitingTask() {
        // 优先处理比赛的提交任务
        // 其次处理普通提交的提交任务
        // 最后处理在线调试的任务
        handleWaitingTask(Constants.Queue.CONTEST_JUDGE_WAITING.getName(),
                Constants.Queue.GENERAL_JUDGE_WAITING.getName(),
                Constants.Queue.TEST_JUDGE_WAITING.getName());
    }


    @Override
    public String getTaskByRedis(String queue) {
        long size = redisUtils.lGetListSize(queue);
        if (size > 0) {
            return (String) redisUtils.lrPop(queue);
        } else {
            return null;
        }
    }

    @Override
    public void handleJudgeMsg(String taskStr, String queueName) {
        if (Constants.Queue.TEST_JUDGE_WAITING.getName().equals(queueName)) {
            TestJudgeReq testJudgeReq = JSONUtil.toBean(taskStr, TestJudgeReq.class);
            dispatcher.dispatch(Constants.TaskType.TEST_JUDGE, testJudgeReq);
        } else {
            JSONObject task = JSONUtil.parseObj(taskStr);
            Long judgeId = task.getLong("judgeId");
            Judge judge = judgeEntityService.getById(judgeId);
            if (judge != null) {
                // 调度评测时发现该评测任务被取消，则结束评测
                if (Objects.equals(judge.getStatus(), Constants.Judge.STATUS_CANCELLED.getStatus())) {
                    if (judge.getCid() != 0) {
                        UpdateWrapper<ContestRecord> updateWrapper = new UpdateWrapper<>();
                        // 取消评测，不罚时也不算得分
                        updateWrapper.set("status", Constants.Contest.RECORD_NOT_AC_NOT_PENALTY.getCode());
                        updateWrapper.eq("submit_id", judge.getSubmitId()); // submit_id一定只有一个
                        contestRecordEntityService.update(updateWrapper);
                    }
                } else {
                    String token = task.getStr("token");
                    // 调用判题服务
                    dispatcher.dispatch(Constants.TaskType.JUDGE, new ToJudgeDTO()
                            .setJudge(judge)
                            .setToken(token)
                            .setRemoteJudgeProblem(null));
                }
            }

        }
        // 接着处理任务
        processWaitingTask();
    }

}