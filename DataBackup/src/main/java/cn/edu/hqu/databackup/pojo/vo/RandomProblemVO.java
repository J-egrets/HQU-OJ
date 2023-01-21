package cn.edu.hqu.databackup.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: egret
 */
@Data
public class RandomProblemVO {

    @ApiModelProperty(value = "题目id")
    private String problemId;
}