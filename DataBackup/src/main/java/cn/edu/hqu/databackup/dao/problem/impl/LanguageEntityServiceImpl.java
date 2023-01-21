package cn.edu.hqu.databackup.dao.problem.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.LanguageMapper;
import cn.edu.hqu.api.pojo.entity.problem.Language;
import cn.edu.hqu.databackup.dao.problem.LanguageEntityService;

/**
 * @Author: egret
 */
@Service
public class LanguageEntityServiceImpl extends ServiceImpl<LanguageMapper, Language> implements LanguageEntityService {
}