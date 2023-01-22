package cn.edu.hqu.judgeserver.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.judgeserver.mapper.JudgeMapper;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.judgeserver.dao.JudgeEntityService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author egret
 */
@Service
public class JudgeEntityServiceImpl extends ServiceImpl<JudgeMapper, Judge> implements JudgeEntityService {

}
