package cn.edu.hqu.judgeserver.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;;
import cn.edu.hqu.judgeserver.mapper.ContestMapper;

import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.judgeserver.dao.ContestEntityService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author egret
 */
@Service
public class ContestEntityServiceImpl extends ServiceImpl<ContestMapper, Contest> implements ContestEntityService {

}
