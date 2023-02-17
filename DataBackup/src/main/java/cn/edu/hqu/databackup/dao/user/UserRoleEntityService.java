package cn.edu.hqu.databackup.dao.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.api.pojo.entity.user.Role;
import cn.edu.hqu.api.pojo.entity.user.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.databackup.pojo.vo.UserRolesVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @Author: egret
 */
public interface UserRoleEntityService extends IService<UserRole> {

    UserRolesVO getUserRoles(String uid, String username, String openId);

    List<Role> getRolesByUid(String uid);

    IPage<UserRolesVO> getUserList(int limit, int currentPage, String keyword, Boolean onlyAdmin);

    void deleteCache(String uid, boolean isRemoveSession);

    String getAuthChangeContent(int oldType,int newType);
}
