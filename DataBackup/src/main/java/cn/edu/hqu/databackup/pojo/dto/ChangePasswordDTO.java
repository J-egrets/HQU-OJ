package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

/**
 * @Author: egret
 */
@Data
public class ChangePasswordDTO {

    private String oldPassword;

    private String newPassword;
}