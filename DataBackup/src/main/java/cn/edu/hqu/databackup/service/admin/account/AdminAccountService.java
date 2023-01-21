package cn.edu.hqu.databackup.service.admin.account;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.LoginDTO;
import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;

/**
 * @Author: egret
 */
public interface AdminAccountService {

    public CommonResult<UserInfoVO> login(LoginDTO loginDto);

    public CommonResult<Void> logout();
}