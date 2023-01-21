package cn.edu.hqu.databackup.dao.problem.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.CodeTemplateMapper;
import cn.edu.hqu.api.pojo.entity.problem.CodeTemplate;
import cn.edu.hqu.databackup.dao.problem.CodeTemplateEntityService;

/**
 * @Author: egret
 */
@Service
public class CodeTemplateEntityServiceImpl extends ServiceImpl<CodeTemplateMapper, CodeTemplate> implements CodeTemplateEntityService {
}