package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

/**
 * @Author: egret
 */
@Data
public class ChangeEmailDTO {

    private String password;

    private String newEmail;

    private String code;
}