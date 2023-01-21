package cn.edu.hqu.databackup.service.admin.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.admin.user.AdminUserManager;
import cn.edu.hqu.databackup.pojo.dto.AdminEditUserDTO;
import cn.edu.hqu.databackup.pojo.vo.UserRolesVO;
import cn.edu.hqu.databackup.service.admin.user.AdminUserService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: egret
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserManager adminUserManager;

    @Override
    public CommonResult<IPage<UserRolesVO>> getUserList(Integer limit, Integer currentPage, Boolean onlyAdmin, String keyword) {
        return CommonResult.successResponse(adminUserManager.getUserList(limit, currentPage, onlyAdmin, keyword));
    }

    @Override
    public CommonResult<Void> editUser(AdminEditUserDTO adminEditUserDto) {
        try {
            adminUserManager.editUser(adminEditUserDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteUser(List<String> deleteUserIdList) {
        try {
            adminUserManager.deleteUser(deleteUserIdList);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> insertBatchUser(List<List<String>> users) {
        try {
            adminUserManager.insertBatchUser(users);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Map<Object, Object>> generateUser(Map<String, Object> params) {
        try {
            return CommonResult.successResponse(adminUserManager.generateUser(params));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}