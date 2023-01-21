package cn.edu.hqu.databackup.controller.oj;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cn.edu.hqu.databackup.annotation.AnonApi;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ApplyResetPasswordDTO;
import cn.edu.hqu.databackup.pojo.dto.LoginDTO;
import cn.edu.hqu.databackup.pojo.dto.RegisterDTO;
import cn.edu.hqu.databackup.pojo.dto.ResetPasswordDTO;
import cn.edu.hqu.databackup.pojo.vo.RegisterCodeVO;
import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;
import cn.edu.hqu.databackup.service.oj.PassportService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: egret
 * @Description: 处理登录、注册、重置密码
 */
@RestController
@RequestMapping("/api")
public class PassportController {


    @Autowired
    private PassportService passportService;

    /**
     * @param loginDto
     * @MethodName login
     * @Description 处理登录逻辑
     * @Return CommonResult
     * @Since 2020/10/24
     */
    @PostMapping("/login")
    @AnonApi
    public CommonResult<UserInfoVO> login(@Validated @RequestBody LoginDTO loginDto, HttpServletResponse response, HttpServletRequest request) {
        return passportService.login(loginDto, response, request);
    }

    /**
     * @MethodName getRegisterCode
     * @Description 调用邮件服务，发送注册流程的6位随机验证码
     * @Return
     * @Since 2020/10/26
     */
    @RequestMapping(value = "/get-register-code", method = RequestMethod.GET)
    @AnonApi
    public CommonResult<RegisterCodeVO> getRegisterCode(@RequestParam(value = "email", required = true) String email) {
        return passportService.getRegisterCode(email);
    }


    /**
     * @param registerDto
     * @MethodName register
     * @Description 注册逻辑，具体参数请看RegisterDto类
     * @Return
     * @Since 2020/10/24
     */
    @PostMapping("/register")
    @AnonApi
    public CommonResult<Void> register(@Validated @RequestBody RegisterDTO registerDto) {
        return passportService.register(registerDto);
    }


    /**
     * @param applyResetPasswordDto
     * @MethodName applyResetPassword
     * @Description 发送重置密码的链接邮件
     * @Return
     * @Since 2020/11/6
     */
    @PostMapping("/apply-reset-password")
    @AnonApi
    public CommonResult<Void> applyResetPassword(@RequestBody ApplyResetPasswordDTO applyResetPasswordDto) {
        return passportService.applyResetPassword(applyResetPasswordDto);
    }


    /**
     * @param resetPasswordDto
     * @MethodName resetPassword
     * @Description 用户重置密码
     * @Return
     * @Since 2020/11/6
     */
    @PostMapping("/reset-password")
    @AnonApi
    public CommonResult<Void> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDto) {
        return passportService.resetPassword(resetPasswordDto);
    }


    /**
     * @MethodName logout
     * @Description 退出逻辑，将jwt在redis中清除，下次需要再次登录。
     * @Return CommonResult
     * @Since 2020/10/24
     */
    @GetMapping("/logout")
    @RequiresAuthentication
    public CommonResult<Void> logout() {
        return passportService.logout();
    }

}