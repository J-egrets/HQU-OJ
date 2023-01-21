package cn.edu.hqu.databackup.service.oj;


import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ApplyResetPasswordDTO;
import cn.edu.hqu.databackup.pojo.dto.LoginDTO;
import cn.edu.hqu.databackup.pojo.dto.RegisterDTO;
import cn.edu.hqu.databackup.pojo.dto.ResetPasswordDTO;
import cn.edu.hqu.databackup.pojo.vo.RegisterCodeVO;
import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: egret
 */
public interface PassportService {

    public CommonResult<UserInfoVO> login(LoginDTO loginDto, HttpServletResponse response, HttpServletRequest request);

    public CommonResult<RegisterCodeVO> getRegisterCode(String email);

    public CommonResult<Void> register(RegisterDTO registerDto);

    public CommonResult<Void> applyResetPassword(ApplyResetPasswordDTO applyResetPasswordDto);

    public CommonResult<Void> resetPassword(ResetPasswordDTO resetPasswordDto);

    public CommonResult<Void> logout();
}