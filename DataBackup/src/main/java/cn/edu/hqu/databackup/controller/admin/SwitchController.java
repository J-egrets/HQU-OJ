package cn.edu.hqu.databackup.controller.admin;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.SwitchConfigDTO;
import cn.edu.hqu.databackup.service.admin.system.ConfigService;

import javax.annotation.Resource;

/**
 * @author egret
 */
@RestController
@RequestMapping("/api/admin/switch")
public class SwitchController {

    @Resource
    private ConfigService configService;

    @RequiresPermissions("system_info_admin")
    @RequestMapping("/info")
    public CommonResult<SwitchConfigDTO> getSwitchConfig() {

        return configService.getSwitchConfig();
    }

    @RequiresPermissions("system_info_admin")
    @PutMapping("/update")
    public CommonResult<Void> setSwitchConfig(@RequestBody SwitchConfigDTO config) {
        return configService.setSwitchConfig(config);
    }
}
