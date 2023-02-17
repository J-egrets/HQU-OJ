package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 微信登录DTO
 * @author egret
 */
@Data
public class WxLoginDTO implements Serializable {

    /**
     * 微信登录openId
     */
    @NotBlank(message = "openId不能为空")
    public String openId;
}
