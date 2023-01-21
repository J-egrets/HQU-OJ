package cn.edu.hqu.databackup.dao.problem.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.dao.problem.TagClassificationEntityService;
import cn.edu.hqu.databackup.mapper.TagClassificationMapper;
import cn.edu.hqu.api.pojo.entity.problem.TagClassification;

/**
 * @Author: egret
 */
@Service
public class TagClassificationEntityServiceImpl extends ServiceImpl<TagClassificationMapper, TagClassification> implements TagClassificationEntityService {
}
