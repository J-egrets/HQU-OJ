package cn.edu.hqu.databackup.crawler.problem;

import lombok.Data;
import lombok.experimental.Accessors;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.api.pojo.entity.problem.Tag;
import cn.edu.hqu.databackup.utils.Constants;

import java.util.List;

/**
 * @author egret
 */
public abstract class ProblemStrategy {

    public abstract RemoteProblemInfo getProblemInfo(String problemId, String author) throws Exception;

    public RemoteProblemInfo getProblemInfoByLogin(String problemId, String author, String username, String password) throws Exception {
        return null;
    }

    @Data
    @Accessors(chain = true)
    public static
    class RemoteProblemInfo {
        private Problem problem;
        private List<Tag> tagList;
        private List<String> langIdList;
        private Constants.RemoteOJ remoteOJ;
    }
}
