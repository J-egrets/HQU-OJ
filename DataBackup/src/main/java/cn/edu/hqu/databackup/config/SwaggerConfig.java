package cn.edu.hqu.databackup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * @author egret
 */
@Configuration
@EnableSwagger2 // 开启swagger2
@Profile({"dev", "test","prod"}) // 只允许开发环境访问
public class SwaggerConfig {

    /**
     * 配置swagger的docket的bean实例
     * @param environment
     * @return
     */
    @Bean
    public Docket docket(Environment environment) {
        // 设置要显示的swagger环境
        // 线下环境
        Profiles profiles = Profiles.of("dev", "test","prod");
        // 通过环境判断是否在自己所设定的环境当中
        boolean flag = environment.acceptsProfiles(profiles);
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("hoj") //分组
                .enable(flag) //开启
                .select()
                //RequestHandlerSelectors扫描方式
                //any()全部
                //none 都不扫描
                //path 过滤什么路径
                .apis(RequestHandlerSelectors.basePackage("cn.edu.hqu.databackup"))
                .build();
    }

    //配置swagger信息
    private ApiInfo apiInfo() {
        //作者信息
        Contact contact = new Contact("egret",
                "https://jegret.cn",
                "1448952248@qq.com");
        return new ApiInfo(
                "HOJ-Backend的API文档",
                "HCODE ONLINE JUDGE(HOJ)的后端接口文档",
                "v4.4",
                "https://jegret.cn",
                contact,
                "MIT",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
    }
}