package cn.edu.hqu.judgeserver.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.judgeserver.mapper.ProblemMapper;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.judgeserver.dao.ProblemEntityService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author egret
 */
@Service
public class ProblemEntityServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemEntityService {

}
