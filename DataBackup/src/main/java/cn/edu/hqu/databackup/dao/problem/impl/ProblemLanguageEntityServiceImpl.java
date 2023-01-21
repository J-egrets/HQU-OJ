package cn.edu.hqu.databackup.dao.problem.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.ProblemLanguageMapper;
import cn.edu.hqu.api.pojo.entity.problem.ProblemLanguage;
import cn.edu.hqu.databackup.dao.problem.ProblemLanguageEntityService;

/**
 * @Author: egret
 */
@Service
public class ProblemLanguageEntityServiceImpl extends ServiceImpl<ProblemLanguageMapper, ProblemLanguage> implements ProblemLanguageEntityService {
}