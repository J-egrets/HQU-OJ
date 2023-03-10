package cn.edu.hqu.databackup.manager.admin.account;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import cn.edu.hqu.databackup.common.exception.StatusAccessDeniedException;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.dao.user.SessionEntityService;
import cn.edu.hqu.databackup.dao.user.UserRoleEntityService;
import cn.edu.hqu.databackup.pojo.dto.LoginDTO;
import cn.edu.hqu.api.pojo.entity.user.Role;
import cn.edu.hqu.api.pojo.entity.user.Session;
import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;
import cn.edu.hqu.databackup.pojo.vo.UserRolesVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.utils.IpUtils;
import cn.edu.hqu.databackup.utils.JwtUtils;
import cn.edu.hqu.databackup.utils.RedisUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Component
public class AdminAccountManager {

    @Autowired
    private SessionEntityService sessionEntityService;

    @Autowired
    private JwtUtils jwtUtils;

    @Resource
    private RedisUtils redisUtils;

    @Autowired
    private UserRoleEntityService userRoleEntityService;

    public UserInfoVO login(LoginDTO loginDto) throws StatusFailException, StatusAccessDeniedException {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response = servletRequestAttributes.getResponse();

        String userIpAddr = IpUtils.getUserIpAddr(request);
        String key = Constants.Account.TRY_LOGIN_NUM.getCode() + loginDto.getUsername() + "_" + userIpAddr;
        Integer tryLoginCount = (Integer) redisUtils.get(key);

        if (tryLoginCount != null && tryLoginCount >= 20) {
            throw new StatusFailException("???????????????????????????????????????????????????????????????????????????????????????????????????");
        }

        UserRolesVO userRolesVo = userRoleEntityService.getUserRoles(null, loginDto.getUsername(),null);

        if (userRolesVo == null) {
            throw new StatusFailException("????????????????????????");
        }

        if (!userRolesVo.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
            if (tryLoginCount == null) {
                redisUtils.set(key, 1, 60 * 30); // ??????????????????????????????????????????????????????
            } else {
                redisUtils.set(key, tryLoginCount + 1, 60 * 30);
            }
            throw new StatusFailException("????????????????????????");
        }

        if (userRolesVo.getStatus() != 0) {
            throw new StatusFailException("?????????????????????????????????????????????????????????");
        }

        // ?????????????????????????????????
        if (tryLoginCount != null) {
            redisUtils.del(key);
        }

        // ??????????????????
        List<String> rolesList = new LinkedList<>();
        userRolesVo.getRoles().stream()
                .forEach(role -> rolesList.add(role.getRole()));


        if (rolesList.contains("admin") || rolesList.contains("root") || rolesList.contains("problem_admin")) { // ?????????????????????????????????????????????
            String jwt = jwtUtils.generateToken(userRolesVo.getUid());

            response.setHeader("Authorization", jwt); //??????????????????
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            // ????????????
            sessionEntityService.save(new Session().setUid(userRolesVo.getUid())
                    .setIp(IpUtils.getUserIpAddr(request)).setUserAgent(request.getHeader("User-Agent")));
            // ??????????????????????????????
            sessionEntityService.checkRemoteLogin(userRolesVo.getUid());

            UserInfoVO userInfoVo = new UserInfoVO();
            BeanUtil.copyProperties(userRolesVo, userInfoVo, "roles");
            userInfoVo.setRoleList(userRolesVo.getRoles()
                    .stream()
                    .map(Role::getRole)
                    .collect(Collectors.toList()));

            return userInfoVo;
        } else {
            throw new StatusAccessDeniedException("????????????????????????????????????????????????");
        }
    }

    public void logout() {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        jwtUtils.cleanToken(userRolesVo.getUid());
        SecurityUtils.getSubject().logout();
    }
}