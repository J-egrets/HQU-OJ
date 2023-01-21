package cn.edu.hqu.databackup.service.admin.system.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.admin.system.DashboardManager;
import cn.edu.hqu.api.pojo.entity.user.Session;
import cn.edu.hqu.databackup.service.admin.system.DashboardService;

import java.util.Map;

/**
 * @Author: egret
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardManager dashboardManager;

    @Override
    public CommonResult<Session> getRecentSession() {
        return CommonResult.successResponse(dashboardManager.getRecentSession());
    }

    @Override
    public CommonResult<Map<Object, Object>> getDashboardInfo() {
        return CommonResult.successResponse(dashboardManager.getDashboardInfo());
    }
}