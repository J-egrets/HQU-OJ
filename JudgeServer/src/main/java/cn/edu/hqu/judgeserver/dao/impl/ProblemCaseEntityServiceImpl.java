package cn.edu.hqu.judgeserver.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.judgeserver.mapper.ProblemCaseMapper;
import cn.edu.hqu.api.pojo.entity.problem.ProblemCase;
import cn.edu.hqu.judgeserver.dao.ProblemCaseEntityService;

/**
 * @author egret
 */
@Service
public class ProblemCaseEntityServiceImpl extends ServiceImpl<ProblemCaseMapper, ProblemCase> implements ProblemCaseEntityService {
}