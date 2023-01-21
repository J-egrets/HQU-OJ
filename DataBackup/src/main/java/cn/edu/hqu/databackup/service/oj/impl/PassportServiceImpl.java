package cn.edu.hqu.databackup.service.oj.impl;

import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusAccessDeniedException;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.oj.PassportManager;
import cn.edu.hqu.databackup.pojo.dto.ApplyResetPasswordDTO;
import cn.edu.hqu.databackup.pojo.dto.LoginDTO;
import cn.edu.hqu.databackup.pojo.dto.RegisterDTO;
import cn.edu.hqu.databackup.pojo.dto.ResetPasswordDTO;
import cn.edu.hqu.databackup.pojo.vo.RegisterCodeVO;
import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;
import cn.edu.hqu.databackup.service.oj.PassportService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: egret
 */
@Service
public class PassportServiceImpl implements PassportService {

    @Resource
    private PassportManager passportManager;

    @Override
    public CommonResult<UserInfoVO> login(LoginDTO loginDto, HttpServletResponse response, HttpServletRequest request) {
        try {
            return CommonResult.successResponse(passportManager.login(loginDto, response, request));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<RegisterCodeVO> getRegisterCode(String email) {
        try {
            return CommonResult.successResponse(passportManager.getRegisterCode(email));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> register(RegisterDTO registerDto) {
        try {
            passportManager.register(registerDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        }
    }

    @Override
    public CommonResult<Void> applyResetPassword(ApplyResetPasswordDTO applyResetPasswordDto) {
        try {
            passportManager.applyResetPassword(applyResetPasswordDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> resetPassword(ResetPasswordDTO resetPasswordDto) {
        try {
            passportManager.resetPassword(resetPasswordDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> logout() {
        passportManager.logout();
        return CommonResult.successResponse("登出成功");
    }
}