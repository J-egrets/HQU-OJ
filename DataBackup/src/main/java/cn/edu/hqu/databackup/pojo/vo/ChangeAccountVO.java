package cn.edu.hqu.databackup.pojo.vo;

import lombok.Data;

/**
 * @Author: egret
 */
@Data
public class ChangeAccountVO {

    private Integer code;

    private String msg;

    private UserInfoVO userInfo;
}