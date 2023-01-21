package cn.edu.hqu.databackup.service.admin.account.impl;

import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusAccessDeniedException;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.admin.account.AdminAccountManager;
import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;
import cn.edu.hqu.databackup.service.admin.account.AdminAccountService;
import cn.edu.hqu.databackup.pojo.dto.LoginDTO;

import javax.annotation.Resource;

/**
 * @Author: egret
 */

@Service
public class AdminAccountServiceImpl implements AdminAccountService {

    @Resource
    private AdminAccountManager adminAccountManager;

    @Override
    public CommonResult<UserInfoVO> login(LoginDTO loginDto) {
        try {
            return CommonResult.successResponse(adminAccountManager.login(loginDto));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        }
    }

    @Override
    public CommonResult<Void> logout() {
        adminAccountManager.logout();
        return CommonResult.successResponse("退出登录成功！");
    }
}