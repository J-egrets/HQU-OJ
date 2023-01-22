package cn.edu.hqu.judgeserver.remoteJudge.task;

import lombok.Getter;
import lombok.Setter;
import cn.edu.hqu.judgeserver.remoteJudge.entity.RemoteJudgeDTO;
import cn.edu.hqu.judgeserver.remoteJudge.entity.RemoteJudgeRes;

/**
 * 远程评测抽象类
 */
public abstract class RemoteJudgeStrategy {

    @Setter
    @Getter
    private RemoteJudgeDTO remoteJudgeDTO;

    public abstract void submit();

    public abstract RemoteJudgeRes result();

    public abstract void login();

    public abstract String getLanguage(String language);

}
