package cn.edu.hqu.databackup.service.admin.system;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.user.Session;

import java.util.Map;

/**
 * @Author: egret
 */
public interface DashboardService {

    public CommonResult<Session> getRecentSession();

    public CommonResult<Map<Object,Object>> getDashboardInfo();
}