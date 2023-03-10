package cn.edu.hqu.judgeserver.remoteJudge;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import cn.edu.hqu.judgeserver.dao.JudgeCaseEntityService;
import cn.edu.hqu.judgeserver.dao.JudgeEntityService;
import cn.edu.hqu.judgeserver.judge.JudgeContext;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.judgeserver.remoteJudge.entity.RemoteJudgeDTO;
import cn.edu.hqu.judgeserver.remoteJudge.entity.RemoteJudgeRes;
import cn.edu.hqu.judgeserver.remoteJudge.task.RemoteJudgeStrategy;
import cn.edu.hqu.judgeserver.service.RemoteJudgeService;
import cn.edu.hqu.judgeserver.util.Constants;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author egret
 */
@Slf4j(topic = "hoj")
@Component
public class RemoteJudgeGetResult {

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private JudgeContext judgeContext;

    @Autowired
    private RemoteJudgeService remoteJudgeService;

    @Resource
    private JudgeCaseEntityService judgeCaseEntityService;

    private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    private final static Map<String, Future> futureTaskMap = new ConcurrentHashMap<>(Runtime.getRuntime().availableProcessors() * 2);

    public void process(RemoteJudgeStrategy remoteJudgeStrategy) {

        RemoteJudgeDTO remoteJudgeDTO = remoteJudgeStrategy.getRemoteJudgeDTO();
        String key = UUID.randomUUID().toString() + remoteJudgeDTO.getSubmitId();
        AtomicInteger count = new AtomicInteger(0);
        Runnable getResultTask = new Runnable() {
            @Override
            public void run() {

                if (count.get() >= 60) { // ??????60??????????????????????????????
                    // ??????????????????????????????????????????
                    UpdateWrapper<Judge> judgeUpdateWrapper = new UpdateWrapper<>();
                    judgeUpdateWrapper.set("status", Constants.Judge.STATUS_SUBMITTED_FAILED.getStatus())
                            .set("error_message", "Waiting for remote judge result exceeds the maximum number of times, please try submitting again!")
                            .eq("submit_id", remoteJudgeDTO.getJudgeId());
                    judgeEntityService.update(judgeUpdateWrapper);

                    log.error("[{}] Get Result Failed!", remoteJudgeDTO.getOj());
                    changeRemoteJudgeLock(remoteJudgeDTO.getOj(),
                            remoteJudgeDTO.getUsername(),
                            remoteJudgeDTO.getServerIp(),
                            remoteJudgeDTO.getServerPort(),
                            remoteJudgeDTO.getSubmitId());

                    Future future = futureTaskMap.get(key);
                    if (future != null) {
                        boolean isCanceled = future.cancel(true);
                        if (isCanceled) {
                            futureTaskMap.remove(key);
                        }
                    }
                    return;
                }

                count.getAndIncrement();

                RemoteJudgeRes remoteJudgeRes;
                try {
                    remoteJudgeRes = remoteJudgeStrategy.result();
                } catch (Exception e) {
                    if (count.get() == 60) {
                        log.error("The Error of getting the `remote judge` result:", e);
                    }
                    return;
                }

                // ????????????????????????????????????
                if (!CollectionUtils.isEmpty(remoteJudgeRes.getJudgeCaseList())) {
                    judgeCaseEntityService.saveBatch(remoteJudgeRes.getJudgeCaseList());
                }

                Integer status = remoteJudgeRes.getStatus();
                if (status.intValue() != Constants.Judge.STATUS_PENDING.getStatus() &&
                        status.intValue() != Constants.Judge.STATUS_JUDGING.getStatus() &&
                        status.intValue() != Constants.Judge.STATUS_COMPILING.getStatus()) {
                    log.info("[{}] Get Result Successfully! Status:[{}]", remoteJudgeDTO.getOj(), status);

                    changeRemoteJudgeLock(remoteJudgeDTO.getOj(),
                            remoteJudgeDTO.getUsername(),
                            remoteJudgeDTO.getServerIp(),
                            remoteJudgeDTO.getServerPort(),
                            remoteJudgeDTO.getSubmitId());

                    Integer time = remoteJudgeRes.getTime();
                    Integer memory = remoteJudgeRes.getMemory();
                    String errorInfo = remoteJudgeRes.getErrorInfo();
                    Judge finalJudgeRes = new Judge();

                    finalJudgeRes.setSubmitId(remoteJudgeDTO.getJudgeId())
                            .setStatus(status)
                            .setTime(time)
                            .setMemory(memory);

                    if (status.intValue() == Constants.Judge.STATUS_COMPILE_ERROR.getStatus()) {
                        finalJudgeRes.setErrorMessage(errorInfo);
                    } else if (status.intValue() == Constants.Judge.STATUS_SYSTEM_ERROR.getStatus()) {
                        finalJudgeRes.setErrorMessage("There is something wrong with the " + remoteJudgeDTO.getOj() + ", please try again later");
                    }

                    // ??????????????????????????????????????????OI??????????????? ???AC???100 ???????????????0???
                    if (remoteJudgeDTO.getCid() != 0) {
                        int score = 0;

                        if (Objects.equals(finalJudgeRes.getStatus(), Constants.Judge.STATUS_ACCEPTED.getStatus())) {
                            score = 100;
                        }

                        finalJudgeRes.setScore(score);
                        // ???????????????
                        judgeEntityService.updateById(finalJudgeRes);
                        // ???????????????
                        judgeContext.updateOtherTable(remoteJudgeDTO.getJudgeId(),
                                status,
                                remoteJudgeDTO.getCid(),
                                remoteJudgeDTO.getUid(),
                                remoteJudgeDTO.getPid(),
                                remoteJudgeDTO.getGid(),
                                score,
                                finalJudgeRes.getTime());

                    } else {
                        judgeEntityService.updateById(finalJudgeRes);
                        // ???????????????
                        judgeContext.updateOtherTable(remoteJudgeDTO.getJudgeId(),
                                status,
                                remoteJudgeDTO.getCid(),
                                remoteJudgeDTO.getUid(),
                                remoteJudgeDTO.getPid(),
                                remoteJudgeDTO.getGid(),
                                null,
                                null);
                    }

                    Future future = futureTaskMap.get(key);
                    if (future != null) {
                        future.cancel(true);
                        futureTaskMap.remove(key);
                    }
                } else {

                    Judge judge = new Judge();
                    judge.setSubmitId(remoteJudgeDTO.getJudgeId())
                            .setStatus(status);
                    // ???????????????
                    judgeEntityService.updateById(judge);
                }
            }
        };
        ScheduledFuture<?> beeperHandle = scheduler.scheduleWithFixedDelay(
                getResultTask, 0, 2500, TimeUnit.MILLISECONDS);
        futureTaskMap.put(key, beeperHandle);
    }


    private void changeRemoteJudgeLock(String remoteJudge, String username, String ip, Integer port, Long resultSubmitId) {
        log.info("After Get Result,remote_judge:[{}],submit_id: [{}]! Begin to return the account to other task!",
                remoteJudge, resultSubmitId);
        // ?????????????????????
        remoteJudgeService.changeAccountStatus(remoteJudge, username);
        if (RemoteJudgeContext.openCodeforcesFixServer) {
            if (remoteJudge.equals(Constants.RemoteJudge.GYM_JUDGE.getName())
                    || remoteJudge.equals(Constants.RemoteJudge.CF_JUDGE.getName())) {
                log.info("After Get Result,remote_judge:[{}],submit_id: [{}] !Begin to return the Server Status to other task!",
                        remoteJudge, resultSubmitId);
                remoteJudgeService.changeServerSubmitCFStatus(ip, port);
            }
        }
    }


}
