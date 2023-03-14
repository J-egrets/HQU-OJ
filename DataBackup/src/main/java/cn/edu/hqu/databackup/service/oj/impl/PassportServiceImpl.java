package cn.edu.hqu.databackup.service.oj.impl;

import cn.edu.hqu.databackup.pojo.dto.*;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusAccessDeniedException;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.oj.PassportManager;
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
    public CommonResult<JSONObject> getQrCode(Boolean bind) {
        try {
            return CommonResult.successResponse(passportManager.getQrCode(bind));
        } catch (Exception e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public String checkSign(HttpServletRequest request) {
        try {
            return passportManager.checkSign(request);
        } catch (Exception e) {
            return "fail";
        }
    }

    @Override
    public String oauthInvoke(HttpServletRequest request) {
        try {
            return passportManager.oauthInvoke(request);
        } catch (Exception e) {
            return "注册失败！请联系管理员排查原因！";
        }
    }

    @Override
    public CommonResult<UserInfoVO> wxLogin(WxLoginDTO wxLoginDTO, HttpServletResponse response, HttpServletRequest request) {
        try {
            UserInfoVO userInfoVO = passportManager.wxLogin(wxLoginDTO, response, request);
            if (userInfoVO != null) {
                return CommonResult.successResponse(userInfoVO);
            } else {
                return CommonResult.errorResponse("登录中", 202);
            }
        } catch (Exception e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<UserInfoVO> checkWxBind(WxBindDTO wxLoginDTO) {
        try {
            return CommonResult.successResponse(passportManager.checkWxBind(wxLoginDTO));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

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