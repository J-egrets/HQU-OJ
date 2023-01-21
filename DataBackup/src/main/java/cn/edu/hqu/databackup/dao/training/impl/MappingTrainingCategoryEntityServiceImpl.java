package cn.edu.hqu.databackup.dao.training.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.MappingTrainingCategoryMapper;
import cn.edu.hqu.api.pojo.entity.training.MappingTrainingCategory;
import cn.edu.hqu.databackup.dao.training.MappingTrainingCategoryEntityService;

/**
 * @Author: egret
 */
@Service
public class MappingTrainingCategoryEntityServiceImpl extends ServiceImpl<MappingTrainingCategoryMapper, MappingTrainingCategory> implements MappingTrainingCategoryEntityService {
}