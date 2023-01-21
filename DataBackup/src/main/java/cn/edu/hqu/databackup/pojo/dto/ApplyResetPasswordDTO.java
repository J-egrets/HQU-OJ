package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

/**
 * @Author: egret
 */
@Data
public class ApplyResetPasswordDTO {

    private String captcha;

    private String captchaKey;

    private String email;
}