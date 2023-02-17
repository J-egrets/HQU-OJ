package cn.edu.hqu.databackup.service.oj;


import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.*;
import cn.edu.hqu.databackup.pojo.vo.RegisterCodeVO;
import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;
import com.alibaba.fastjson.JSONObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: egret
 */
public interface PassportService {

    CommonResult<JSONObject> getQrCode();

    String checkSign (HttpServletRequest request);

    void oauthInvoke(HttpServletRequest request);

    CommonResult<UserInfoVO> wxLogin(@Validated @RequestBody WxLoginDTO wxLoginDTO, HttpServletResponse response, HttpServletRequest request);

    CommonResult<UserInfoVO> login(LoginDTO loginDto, HttpServletResponse response, HttpServletRequest request);

    CommonResult<RegisterCodeVO> getRegisterCode(String email);

    CommonResult<Void> register(RegisterDTO registerDto);

    CommonResult<Void> applyResetPassword(ApplyResetPasswordDTO applyResetPasswordDto);

    CommonResult<Void> resetPassword(ResetPasswordDTO resetPasswordDto);

    CommonResult<Void> logout();
}