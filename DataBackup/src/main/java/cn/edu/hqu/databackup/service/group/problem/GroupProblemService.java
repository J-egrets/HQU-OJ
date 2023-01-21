package cn.edu.hqu.databackup.service.group.problem;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ProblemDTO;
import cn.edu.hqu.api.pojo.dto.CompileDTO;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.api.pojo.entity.problem.ProblemCase;
import cn.edu.hqu.api.pojo.entity.problem.Tag;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @Author: egret
 */
public interface GroupProblemService {

    public CommonResult<IPage<ProblemVO>> getProblemList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<IPage<Problem>> getAdminProblemList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<Problem> getProblem(Long pid);

    public CommonResult<Void> addProblem(ProblemDTO problemDto);

    public CommonResult<Void> updateProblem(ProblemDTO problemDto);

    public CommonResult<Void> deleteProblem(Long pid);

    public CommonResult<List<ProblemCase>> getProblemCases(Long pid, Boolean isUpload);

    public CommonResult<List<Tag>> getAllProblemTagsList(Long gid);

    public CommonResult<Void> compileSpj(CompileDTO compileDTO, Long gid);

    public CommonResult<Void> compileInteractive(CompileDTO compileDTO, Long gid);

    public CommonResult<Void> changeProblemAuth(Long pid, Integer auth);

    public CommonResult<Void> applyPublic(Long pid, Boolean isApplied);
}
