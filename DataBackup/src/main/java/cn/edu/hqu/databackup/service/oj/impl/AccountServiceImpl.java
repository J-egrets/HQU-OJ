package cn.edu.hqu.databackup.service.oj.impl;

import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusSystemErrorException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.oj.AccountManager;
import cn.edu.hqu.databackup.pojo.dto.ChangeEmailDTO;
import cn.edu.hqu.databackup.pojo.dto.ChangePasswordDTO;
import cn.edu.hqu.databackup.pojo.dto.CheckUsernameOrEmailDTO;
import cn.edu.hqu.databackup.pojo.vo.*;
import cn.edu.hqu.databackup.service.oj.AccountService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountManager accountManager;

    @Override
    public CommonResult<CheckUsernameOrEmailVO> checkUsernameOrEmail(CheckUsernameOrEmailDTO checkUsernameOrEmailDto) {
        return CommonResult.successResponse(accountManager.checkUsernameOrEmail(checkUsernameOrEmailDto));
    }

    @Override
    public CommonResult<UserHomeVO> getUserHomeInfo(String uid, String username) {
        try {
            return CommonResult.successResponse(accountManager.getUserHomeInfo(uid, username));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<UserCalendarHeatmapVO> getUserCalendarHeatmap(String uid, String username) {
        try {
            return CommonResult.successResponse(accountManager.getUserCalendarHeatmap(uid, username));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<ChangeAccountVO> changePassword(ChangePasswordDTO changePasswordDto) {
        try {
            return CommonResult.successResponse(accountManager.changePassword(changePasswordDto));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }

    @Override
    public CommonResult<Void> getChangeEmailCode(String email) {
        try {
            accountManager.getChangeEmailCode(email);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<ChangeAccountVO> changeEmail(ChangeEmailDTO changeEmailDto) {
        try {
            return CommonResult.successResponse(accountManager.changeEmail(changeEmailDto));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }

    @Override
    public CommonResult<UserInfoVO> changeUserInfo(UserInfoVO userInfoVo) {
        try {
            return CommonResult.successResponse(accountManager.changeUserInfo(userInfoVo));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<UserAuthInfoVO> getUserAuthInfo() {
        return CommonResult.successResponse(accountManager.getUserAuthInfo());
    }
}