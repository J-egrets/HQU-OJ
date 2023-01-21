package cn.edu.hqu.databackup.dao.training;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.training.TrainingCategory;

/**
 * @author egret
 */
public interface TrainingCategoryEntityService extends IService<TrainingCategory> {

    public TrainingCategory getTrainingCategoryByTrainingId(Long tid);
}
