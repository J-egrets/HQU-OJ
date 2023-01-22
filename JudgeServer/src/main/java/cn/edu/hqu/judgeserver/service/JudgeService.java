package cn.edu.hqu.judgeserver.service;

import cn.edu.hqu.judgeserver.common.exception.SystemError;
import cn.edu.hqu.api.pojo.dto.TestJudgeReq;
import cn.edu.hqu.api.pojo.dto.TestJudgeRes;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.api.pojo.dto.ToJudgeDTO;

import java.util.HashMap;

/**
 * @author egret
 */
public interface JudgeService {

    public void judge(Judge judge);

    public TestJudgeRes testJudge(TestJudgeReq testJudgeReq);

    public void remoteJudge(ToJudgeDTO toJudgeDTO);

    public Boolean compileSpj(String code, Long pid, String spjLanguage, HashMap<String, String> extraFiles) throws SystemError;

    public Boolean compileInteractive(String code, Long pid, String interactiveLanguage, HashMap<String, String> extraFiles) throws SystemError;

}
