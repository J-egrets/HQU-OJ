package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.user.Role;
import cn.edu.hqu.api.pojo.entity.user.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.edu.hqu.databackup.pojo.vo.UserRolesVO;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author: egret
 */
@Mapper
@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {

    UserRolesVO getUserRoles(@Param("uid") String uid, @Param("username") String username, @Param("openId") String openId);

    List<Role> getRolesByUid(@Param("uid") String uid);

    IPage<UserRolesVO> getUserList(Page<UserRolesVO> page, @Param("limit") int limit,
                                   @Param("currentPage") int currentPage,
                                   @Param("keyword") String keyword);

    IPage<UserRolesVO> getAdminUserList(Page<UserRolesVO> page, @Param("limit") int limit,
                                        @Param("currentPage") int currentPage,
                                        @Param("keyword") String keyword);
}
