package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: egret
 */
@Data
public class RegisterContestDTO {

    @NotBlank(message = "cid不能为空")
    private Long cid;

    @NotBlank(message = "password不能为空")
    private String password;
}