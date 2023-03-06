package cn.edu.hqu.databackup.config;

import lombok.Data;
import cn.edu.hqu.databackup.utils.IpUtils;

/**
 * @author egret
 */
@Data
public class WebConfig {

    // 邮箱配置
    private String emailUsername;

    private String emailPassword;

    private String emailHost;

    private Integer emailPort;

    private Boolean emailSsl = true;

    private String emailBGImg = "https://blog-1306930119.cos.ap-shanghai.myqcloud.com/img/HQU_LOGO.png";

    // 网站前端显示配置
    private String baseUrl = "http://" + IpUtils.getServiceIp();

    private String name = "HQU Online Judge";

    private String shortName = "HOJ";

    private String description;

    private Boolean register = true;

    private String recordName;

    private String recordUrl;

    private String projectName = "HOJ";

    private String projectUrl = "https://gitee.com/himitzh0730/hoj";
}
