package cn.edu.hqu.databackup.dao.problem.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.ProblemCaseMapper;
import cn.edu.hqu.api.pojo.entity.problem.ProblemCase;
import cn.edu.hqu.databackup.dao.problem.ProblemCaseEntityService;

/**
 * @Author: egret
 */
@Service
public class ProblemCaseEntityServiceImpl extends ServiceImpl<ProblemCaseMapper, ProblemCase> implements ProblemCaseEntityService {
}