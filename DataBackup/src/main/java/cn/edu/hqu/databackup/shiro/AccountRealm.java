package cn.edu.hqu.databackup.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.edu.hqu.databackup.dao.user.UserInfoEntityService;
import cn.edu.hqu.databackup.dao.user.UserRoleEntityService;
import cn.edu.hqu.databackup.mapper.RoleAuthMapper;
import cn.edu.hqu.api.pojo.entity.user.Auth;
import cn.edu.hqu.api.pojo.entity.user.Role;
import cn.edu.hqu.api.pojo.entity.user.UserInfo;
import cn.edu.hqu.databackup.utils.JwtUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: egret
 */
@Slf4j
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRoleEntityService userRoleEntityService;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private RoleAuthMapper roleAuthMapper;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        AccountProfile user = (AccountProfile) principals.getPrimaryPrincipal();
        //角色权限列表
        List<String> permissionsNameList = new LinkedList<>();
        //用户角色列表
        List<String> roleNameList = new LinkedList<>();
        //获取该用户角色所有的权限
        List<Role> roles = userRoleEntityService.getRolesByUid(user.getUid());
        for (Role role : roles) {
            roleNameList.add(role.getRole());
            for (Auth auth : roleAuthMapper.getRoleAuths(role.getId()).getAuths()) {
                permissionsNameList.add(auth.getPermission());
            }
        }
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        authorizationInfo.addRoles(roleNameList);
        //添加权限
        authorizationInfo.addStringPermissions(permissionsNameList);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        JwtToken jwt = (JwtToken) token;

        String userId = jwtUtils.getClaimByToken((String) jwt.getPrincipal()).getSubject();

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", userId)
                .select("uuid", "username", "nickname", "realname", "title_name", "title_color", "avatar", "status");

        UserInfo userInfo = userInfoEntityService.getOne(queryWrapper, false);
        if (userInfo == null) {
            throw new UnknownAccountException("账户不存在！");
        }
        if (userInfo.getStatus() == 1) {
            throw new LockedAccountException("该账户已被封禁，请联系管理员进行处理！");
        }
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(userInfo, profile);
        profile.setUid(userInfo.getUuid());
        return new SimpleAuthenticationInfo(profile, jwt.getCredentials(), getName());
    }
}