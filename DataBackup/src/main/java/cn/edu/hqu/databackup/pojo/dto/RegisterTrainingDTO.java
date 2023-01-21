package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: egret
 */
@Data
public class RegisterTrainingDTO {

    @NotBlank(message = "tid不能为空")
    private Long tid;

    @NotBlank(message = "password不能为空")
    private String password;
}