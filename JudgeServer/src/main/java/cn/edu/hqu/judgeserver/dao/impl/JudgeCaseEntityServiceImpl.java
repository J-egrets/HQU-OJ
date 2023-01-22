package cn.edu.hqu.judgeserver.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.judgeserver.mapper.JudgeCaseMapper;
import cn.edu.hqu.api.pojo.entity.judge.JudgeCase;
import cn.edu.hqu.judgeserver.dao.JudgeCaseEntityService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author egret
 */
@Service
public class JudgeCaseEntityServiceImpl extends ServiceImpl<JudgeCaseMapper, JudgeCase> implements JudgeCaseEntityService {

}
