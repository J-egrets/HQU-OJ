package cn.edu.hqu.databackup.dao.judge.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.RemoteJudgeAccountMapper;
import cn.edu.hqu.api.pojo.entity.judge.RemoteJudgeAccount;
import cn.edu.hqu.databackup.dao.judge.RemoteJudgeAccountEntityService;

/**
 * @Author: egret
 */
@Service
public class RemoteJudgeAccountEntityServiceImpl extends ServiceImpl<RemoteJudgeAccountMapper, RemoteJudgeAccount> implements RemoteJudgeAccountEntityService {
}