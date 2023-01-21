package cn.edu.hqu.databackup.pojo.vo;

import lombok.Data;
import cn.edu.hqu.api.pojo.entity.judge.JudgeCase;

import java.util.List;

/**
 * @Author: egret
 */
@Data
public class JudgeCaseVO {

    /**
     * 当judgeCaseMode为default时
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 当judgeCaseMode为subtask_lowest,subtask_average时
     */
    private List<SubTaskJudgeCaseVO> subTaskJudgeCaseVoList;

    /**
     * default,subtask_lowest,subtask_average
     */
    private String judgeCaseMode;
}
