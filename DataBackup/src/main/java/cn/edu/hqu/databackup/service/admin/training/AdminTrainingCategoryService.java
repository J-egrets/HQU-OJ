package cn.edu.hqu.databackup.service.admin.training;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.training.TrainingCategory;

/**
 * @Author: egret
 */
public interface AdminTrainingCategoryService {

    public CommonResult<TrainingCategory> addTrainingCategory(TrainingCategory trainingCategory);

    public CommonResult<Void> updateTrainingCategory(TrainingCategory trainingCategory);

    public CommonResult<Void> deleteTrainingCategory(Long cid);
}