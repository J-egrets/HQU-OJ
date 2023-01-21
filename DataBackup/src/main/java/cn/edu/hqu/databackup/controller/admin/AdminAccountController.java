package cn.edu.hqu.databackup.controller.admin;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cn.edu.hqu.databackup.common.result.CommonResult;

import cn.edu.hqu.databackup.pojo.dto.LoginDTO;

import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;
import cn.edu.hqu.databackup.service.admin.account.AdminAccountService;

/**
 * @author egret
 */
@RestController
@RequestMapping("/api/admin")
public class AdminAccountController {

    @Autowired
    private AdminAccountService adminAccountService;

    @PostMapping("/login")
    public CommonResult<UserInfoVO> login(@Validated @RequestBody LoginDTO loginDto){
       return adminAccountService.login(loginDto);
    }

    @GetMapping("/logout")
    @RequiresAuthentication
    @RequiresRoles(value = {"root","admin","problem_admin"},logical = Logical.OR)
    public CommonResult<Void> logout() {
        return adminAccountService.logout();
    }

}