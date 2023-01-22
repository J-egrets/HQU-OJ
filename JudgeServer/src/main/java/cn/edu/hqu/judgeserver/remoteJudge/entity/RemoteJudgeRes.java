package cn.edu.hqu.judgeserver.remoteJudge.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import cn.edu.hqu.api.pojo.entity.judge.JudgeCase;

import java.io.Serializable;
import java.util.List;

/**
 * @author egret
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class RemoteJudgeRes implements Serializable {
    private static final long serialVersionUID = 999L;

    private Integer status;

    private Integer time;

    private Integer memory;

    private String errorInfo;

    private List<JudgeCase> judgeCaseList;
}