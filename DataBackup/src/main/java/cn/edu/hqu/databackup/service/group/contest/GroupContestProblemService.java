package cn.edu.hqu.databackup.service.group.contest;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ContestProblemDTO;
import cn.edu.hqu.databackup.pojo.dto.ProblemDTO;
import cn.edu.hqu.api.pojo.entity.contest.ContestProblem;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: egret
 */
public interface GroupContestProblemService {

    public CommonResult<HashMap<String, Object>> getContestProblemList(Integer limit, Integer currentPage, String keyword, Long cid, Integer problemType, String oj);

    public CommonResult<Map<Object, Object>> addProblem(ProblemDTO problemDto);

    public CommonResult<ContestProblem> getContestProblem(Long pid, Long cid);

    public CommonResult<Void> updateContestProblem(ContestProblem contestProblem);

    public CommonResult<Void> deleteContestProblem(Long pid, Long cid);

    public CommonResult<Void> addProblemFromPublic(ContestProblemDTO contestProblemDto);

    public CommonResult<Void> addProblemFromGroup(String problemId, Long cid, String displayId);

}
