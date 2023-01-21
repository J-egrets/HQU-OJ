package cn.edu.hqu.databackup.service.admin.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.AdminEditUserDTO;
import cn.edu.hqu.databackup.pojo.vo.UserRolesVO;

import java.util.List;
import java.util.Map;

/**
 * @Author: egret
 */
public interface AdminUserService {

    public CommonResult<IPage<UserRolesVO>> getUserList(Integer limit, Integer currentPage, Boolean onlyAdmin, String keyword);

    public CommonResult<Void> editUser(AdminEditUserDTO adminEditUserDto);

    public CommonResult<Void> deleteUser(List<String> deleteUserIdList);

    public CommonResult<Void> insertBatchUser(List<List<String>> users);

    public CommonResult<Map<Object,Object>> generateUser(Map<String, Object> params);

}