package cn.edu.hqu.judgeserver.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.judgeserver.mapper.RemoteJudgeAccountMapper;
import cn.edu.hqu.api.pojo.entity.judge.RemoteJudgeAccount;
import cn.edu.hqu.judgeserver.dao.RemoteJudgeAccountEntityService;

/**
 * @author egret
 */
@Service
public class RemoteJudgeAccountEntityServiceImpl extends ServiceImpl<RemoteJudgeAccountMapper, RemoteJudgeAccount> implements RemoteJudgeAccountEntityService {
}