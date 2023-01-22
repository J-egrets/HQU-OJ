package cn.edu.hqu.judgeserver.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.judge.JudgeServer;

import java.util.HashMap;

/**
 * @author egret
 */
public interface JudgeServerEntityService extends IService<JudgeServer> {

    public HashMap<String, Object> getJudgeServerInfo();
}
