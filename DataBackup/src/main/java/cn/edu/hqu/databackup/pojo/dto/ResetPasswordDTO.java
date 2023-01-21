package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

/**
 * @Author: egret
 */

@Data
public class ResetPasswordDTO {

    private String username;

    private String password;

    private String code;
}