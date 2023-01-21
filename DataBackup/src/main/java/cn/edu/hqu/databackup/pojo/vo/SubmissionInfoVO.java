package cn.edu.hqu.databackup.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.edu.hqu.api.pojo.entity.judge.Judge;

/**
 * @Author: egret
 */
@Data
public class SubmissionInfoVO {

    @ApiModelProperty(value = "提交详情")
    private Judge submission;

    @ApiModelProperty(value = "提交者是否可以分享该代码")
    private Boolean codeShare;
}