package cn.edu.hqu.databackup.service.admin.training.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.admin.training.AdminTrainingCategoryManager;
import cn.edu.hqu.api.pojo.entity.training.TrainingCategory;
import cn.edu.hqu.databackup.service.admin.training.AdminTrainingCategoryService;

/**
 * @Author: egret
 */
@Service
public class AdminTrainingCategoryServiceImpl implements AdminTrainingCategoryService {

    @Autowired
    private AdminTrainingCategoryManager adminTrainingCategoryManager;

    @Override
    public CommonResult<TrainingCategory> addTrainingCategory(TrainingCategory trainingCategory) {
        try {
            return CommonResult.successResponse(adminTrainingCategoryManager.addTrainingCategory(trainingCategory));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateTrainingCategory(TrainingCategory trainingCategory) {
        try {
            adminTrainingCategoryManager.updateTrainingCategory(trainingCategory);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteTrainingCategory(Long cid) {
        try {
            adminTrainingCategoryManager.deleteTrainingCategory(cid);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}