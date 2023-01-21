package cn.edu.hqu.databackup.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: egret
 */
@Data
public class AccessVO {

    @ApiModelProperty(value = "是否有进入比赛或训练的权限")
    private Boolean access;
}