package cn.edu.hqu.databackup.service.oj.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusAccessDeniedException;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.oj.ProblemManager;
import cn.edu.hqu.databackup.pojo.dto.LastAcceptedCodeVO;
import cn.edu.hqu.databackup.pojo.dto.PidListDTO;
import cn.edu.hqu.databackup.pojo.vo.ProblemFullScreenListVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemInfoVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;
import cn.edu.hqu.databackup.pojo.vo.RandomProblemVO;
import cn.edu.hqu.databackup.service.oj.ProblemService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: egret
 */
@Service
public class ProblemServiceImpl implements ProblemService {

    @Resource
    private ProblemManager problemManager;

    @Override
    public CommonResult<Page<ProblemVO>> getProblemList(Integer limit, Integer currentPage, String keyword, List<Long> tagId, Integer difficulty, String oj) {
        return CommonResult.successResponse(problemManager.getProblemList(limit, currentPage, keyword, tagId, difficulty, oj));
    }

    @Override
    public CommonResult<RandomProblemVO> getRandomProblem() {
        try {
            return CommonResult.successResponse(problemManager.getRandomProblem());
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<HashMap<Long, Object>> getUserProblemStatus(PidListDTO pidListDto) {
        try {
            return CommonResult.successResponse(problemManager.getUserProblemStatus(pidListDto));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<ProblemInfoVO> getProblemInfo(String problemId, Long gid) {
        try {
            return CommonResult.successResponse(problemManager.getProblemInfo(problemId, gid));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<LastAcceptedCodeVO> getUserLastAcceptedCode(Long pid, Long cid) {
        return CommonResult.successResponse(problemManager.getUserLastAcceptedCode(pid, cid));
    }

    @Override
    public CommonResult<List<ProblemFullScreenListVO>> getFullScreenProblemList(Long tid, Long cid) {
        try {
            return CommonResult.successResponse(problemManager.getFullScreenProblemList(tid, cid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        }
    }
}