package cn.edu.hqu.databackup.service.admin.system.impl;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.admin.system.ConfigManager;
import cn.edu.hqu.databackup.pojo.dto.*;
import cn.edu.hqu.databackup.service.admin.system.ConfigService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: egret
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigManager configManager;

    @Override
    public CommonResult<JSONObject> getServiceInfo() {
        return CommonResult.successResponse(configManager.getServiceInfo());
    }

    @Override
    public CommonResult<List<JSONObject>> getJudgeServiceInfo() {
        return CommonResult.successResponse(configManager.getJudgeServiceInfo());
    }

    @Override
    public CommonResult<Void> deleteHomeCarousel(Long id) {
        try {
            configManager.deleteHomeCarousel(id);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<WebConfigDTO> getWebConfig() {
        return CommonResult.successResponse(configManager.getWebConfig());
    }

    @Override
    public CommonResult<Void> setWebConfig(WebConfigDTO config) {
        try {
            configManager.setWebConfig(config);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<EmailConfigDTO> getEmailConfig() {
        return CommonResult.successResponse(configManager.getEmailConfig());
    }

    @Override
    public CommonResult<Void> setEmailConfig(EmailConfigDTO config) {
        try {
            configManager.setEmailConfig(config);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> testEmail(TestEmailDTO testEmailDto) {
        try {
            configManager.testEmail(testEmailDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<DBAndRedisConfigDTO> getDBAndRedisConfig() {
        return CommonResult.successResponse(configManager.getDBAndRedisConfig());
    }

    @Override
    public CommonResult<Void> setDBAndRedisConfig(DBAndRedisConfigDTO config) {
        try {
            configManager.setDBAndRedisConfig(config);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<SwitchConfigDTO> getSwitchConfig() {
        return CommonResult.successResponse(configManager.getSwitchConfig());
    }

    @Override
    public CommonResult<Void> setSwitchConfig(SwitchConfigDTO config) {
        try {
            configManager.setSwitchConfig(config);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}