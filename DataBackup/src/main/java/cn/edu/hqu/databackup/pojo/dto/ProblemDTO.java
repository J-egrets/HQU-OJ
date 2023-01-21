package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import cn.edu.hqu.api.pojo.entity.problem.*;

import java.util.List;

/**
 * @Author: egret
 */
@Data
@Accessors(chain = true)
public class ProblemDTO {

    private Problem problem;

    private List<ProblemCase> samples;

    private Boolean isUploadTestCase;

    private String uploadTestcaseDir;

    private String judgeMode;

    private Boolean changeModeCode;

    private Boolean changeJudgeCaseMode;

    private List<Language> languages;

    private List<Tag> tags;

    private List<CodeTemplate> codeTemplates;

}