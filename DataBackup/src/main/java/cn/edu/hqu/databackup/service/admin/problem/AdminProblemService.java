package cn.edu.hqu.databackup.service.admin.problem;

import com.baomidou.mybatisplus.core.metadata.IPage;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ProblemDTO;
import cn.edu.hqu.api.pojo.dto.CompileDTO;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.api.pojo.entity.problem.ProblemCase;
import java.util.List;


/**
 * @author egret
 */
public interface AdminProblemService {

    public CommonResult<IPage<Problem>> getProblemList(Integer limit, Integer currentPage, String keyword, Integer auth, String oj);

    public CommonResult<Problem> getProblem(Long pid);

    public CommonResult<Void> deleteProblem(Long pid);

    public CommonResult<Void> addProblem(ProblemDTO problemDto);

    public CommonResult<Void> updateProblem(ProblemDTO problemDto);

    public CommonResult<List<ProblemCase>> getProblemCases(Long pid, Boolean isUpload);

    public CommonResult compileSpj(CompileDTO compileDTO);

    public CommonResult compileInteractive(CompileDTO compileDTO);

    public CommonResult<Void> importRemoteOJProblem(String name,String problemId);

    public CommonResult<Void> changeProblemAuth(Problem problem);
}
