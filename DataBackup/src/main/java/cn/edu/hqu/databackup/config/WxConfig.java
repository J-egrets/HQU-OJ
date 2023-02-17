package cn.edu.hqu.databackup.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx")
public class WxConfig {

    /**
     * 公众号标识
     */
    private String appId;

    /**
     * 公众号密码
     */
    private String appSecret;

    /**
     * 服务器域名地址，用于微信服务器回调。
     */
    private String server;

    /**
     * 获取code接口
     */
    private String qrCodeUrl;

    /**
     * 获取token接口
     */
    private String tokenUrl;

    /**
     * 获取openid接口
     */
    private String openIdUrl;

    /**
     * 获取用户信息接口
     */
    private String userInfoUrl;

    /**
     * 验证接口的标识
     */
    private String token;
}