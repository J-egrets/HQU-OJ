package cn.edu.hqu.databackup.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.SubmitIdListDTO;
import cn.edu.hqu.databackup.pojo.dto.SubmitJudgeDTO;
import cn.edu.hqu.databackup.pojo.dto.TestJudgeDTO;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.databackup.pojo.vo.JudgeCaseVO;
import cn.edu.hqu.databackup.pojo.vo.JudgeVO;
import cn.edu.hqu.databackup.pojo.vo.SubmissionInfoVO;
import cn.edu.hqu.databackup.pojo.vo.TestJudgeVO;

import java.util.HashMap;

/**
 * @author egret
 */
public interface JudgeService {

    public CommonResult<Judge> submitProblemJudge(SubmitJudgeDTO judgeDto);

    public CommonResult<String> submitProblemTestJudge(TestJudgeDTO testJudgeDto);

    public CommonResult<Judge> resubmit(Long submitId);

    public CommonResult<SubmissionInfoVO> getSubmission(Long submitId);

    public CommonResult<TestJudgeVO> getTestJudgeResult(String testJudgeKey);

    public CommonResult<IPage<JudgeVO>> getJudgeList(Integer limit,
                                                     Integer currentPage,
                                                     Boolean onlyMine,
                                                     String searchPid,
                                                     Integer searchStatus,
                                                     String searchUsername,
                                                     Boolean completeProblemID,
                                                     Long gid);

    public CommonResult<Void> updateSubmission(Judge judge);

    public CommonResult<HashMap<Long, Object>> checkCommonJudgeResult(SubmitIdListDTO submitIdListDto);

    public CommonResult<HashMap<Long, Object>> checkContestJudgeResult(SubmitIdListDTO submitIdListDto);

    public CommonResult<JudgeCaseVO> getALLCaseResult(Long submitId);
}
