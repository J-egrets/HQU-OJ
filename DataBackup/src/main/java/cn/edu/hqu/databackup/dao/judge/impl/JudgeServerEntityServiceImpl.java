package cn.edu.hqu.databackup.dao.judge.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import cn.edu.hqu.databackup.mapper.JudgeServerMapper;

import cn.edu.hqu.api.pojo.entity.judge.JudgeServer;
import cn.edu.hqu.databackup.dao.judge.JudgeServerEntityService;

/**
 * @Author: egret
 */
@Service
public class JudgeServerEntityServiceImpl extends ServiceImpl<JudgeServerMapper, JudgeServer> implements JudgeServerEntityService {

}