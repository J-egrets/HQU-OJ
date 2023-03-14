package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 微信绑定DTO
 * @author egret
 */
@Data
public class WxBindDTO implements Serializable {

    /**
     * 微信绑定sceneStr
     */
    @NotBlank(message = "sceneStr不能为空")
    public String sceneStr;
}
