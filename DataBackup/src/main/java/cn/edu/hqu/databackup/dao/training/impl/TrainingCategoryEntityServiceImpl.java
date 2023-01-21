package cn.edu.hqu.databackup.dao.training.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.TrainingCategoryMapper;
import cn.edu.hqu.api.pojo.entity.training.TrainingCategory;
import cn.edu.hqu.databackup.dao.training.TrainingCategoryEntityService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class TrainingCategoryEntityServiceImpl extends ServiceImpl<TrainingCategoryMapper, TrainingCategory> implements TrainingCategoryEntityService {

    @Resource
    private TrainingCategoryMapper trainingCategoryMapper;

    @Override
    public TrainingCategory getTrainingCategoryByTrainingId(Long tid) {
        return trainingCategoryMapper.getTrainingCategoryByTrainingId(tid);
    }
}