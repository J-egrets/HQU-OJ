package cn.edu.hqu.judgeserver.service;

/**
 * @author egret
 */
public interface RemoteJudgeService {

    public void changeAccountStatus(String remoteJudge, String username);

    public void changeServerSubmitCFStatus(String ip, Integer port);
}
