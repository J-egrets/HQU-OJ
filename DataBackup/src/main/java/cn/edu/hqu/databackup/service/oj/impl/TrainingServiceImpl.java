package cn.edu.hqu.databackup.service.oj.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusAccessDeniedException;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.oj.TrainingManager;
import cn.edu.hqu.databackup.pojo.dto.RegisterTrainingDTO;
import cn.edu.hqu.databackup.pojo.vo.AccessVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;
import cn.edu.hqu.databackup.pojo.vo.TrainingRankVO;
import cn.edu.hqu.databackup.pojo.vo.TrainingVO;
import cn.edu.hqu.databackup.service.oj.TrainingService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: egret
 */
@Service
public class TrainingServiceImpl implements TrainingService {

    @Resource
    private TrainingManager trainingManager;

    @Override
    public CommonResult<IPage<TrainingVO>> getTrainingList(Integer limit, Integer currentPage,
                                                           String keyword, Long categoryId, String auth) {
        return CommonResult.successResponse(trainingManager.getTrainingList(limit, currentPage, keyword, categoryId, auth));
    }

    @Override
    public CommonResult<TrainingVO> getTraining(Long tid) {
        try {
            return CommonResult.successResponse(trainingManager.getTraining(tid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<List<ProblemVO>> getTrainingProblemList(Long tid) {
        try {
            return CommonResult.successResponse(trainingManager.getTrainingProblemList(tid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> toRegisterTraining(RegisterTrainingDTO registerTrainingDto) {
        try {
            trainingManager.toRegisterTraining(registerTrainingDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<AccessVO> getTrainingAccess(Long tid) {
        try {
            return CommonResult.successResponse(trainingManager.getTrainingAccess(tid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<IPage<TrainingRankVO>> getTrainingRank(Long tid, Integer limit, Integer currentPage, String keyword) {
        try {
            return CommonResult.successResponse(trainingManager.getTrainingRank(tid, limit, currentPage, keyword));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }
}