package cn.edu.hqu.databackup.service.admin.rejudge;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.judge.Judge;

/**
 * @author egret
 */
public interface RejudgeService {

    CommonResult<Judge> rejudge(Long submitId);

    CommonResult<Void> rejudgeContestProblem(Long cid, Long pid);

    CommonResult<Judge> manualJudge(Long submitId, Integer status, Integer score);

    CommonResult<Judge> cancelJudge(Long submitId);
}
