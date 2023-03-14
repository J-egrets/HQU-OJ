package cn.edu.hqu.databackup.manager.oj;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.config.WxConfig;
import cn.edu.hqu.databackup.pojo.dto.*;
import cn.edu.hqu.databackup.utils.*;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.hqu.databackup.common.exception.StatusAccessDeniedException;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.config.NacosSwitchConfig;
import cn.edu.hqu.databackup.config.WebConfig;
import cn.edu.hqu.databackup.dao.user.SessionEntityService;
import cn.edu.hqu.databackup.dao.user.UserInfoEntityService;
import cn.edu.hqu.databackup.dao.user.UserRecordEntityService;
import cn.edu.hqu.databackup.dao.user.UserRoleEntityService;
import cn.edu.hqu.databackup.manager.email.EmailManager;
import cn.edu.hqu.databackup.manager.msg.NoticeManager;
import cn.edu.hqu.databackup.pojo.bo.EmailRuleBO;
import cn.edu.hqu.api.pojo.entity.user.*;
import cn.edu.hqu.databackup.pojo.vo.RegisterCodeVO;
import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;
import cn.edu.hqu.databackup.pojo.vo.UserRolesVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Component
@Slf4j(topic = "hoj")
public class PassportManager {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private NacosSwitchConfig nacosSwitchConfig;

    @Resource
    private EmailRuleBO emailRuleBO;

    @Resource
    private UserInfoEntityService userInfoEntityService;

    @Resource
    private UserRoleEntityService userRoleEntityService;

    @Resource
    private UserRecordEntityService userRecordEntityService;

    @Resource
    private SessionEntityService sessionEntityService;

    @Resource
    private EmailManager emailManager;

    @Resource
    private NoticeManager noticeManager;

    @Autowired
    private WxConfig wxConfig;

    @Autowired
    private TextReplyManager textReplyManager;

    /**
     * 获取accessToken
     *
     * @return
     */
    private String getAccessToken() throws Exception {
        //根据appid和appsecret获取access_token
        String getTokenUrl = wxConfig.getTokenUrl().replace("APPID", wxConfig.getAppId()).replace("APPSECRET", wxConfig.getAppSecret());
        String result = HttpClientUtil.doGet(getTokenUrl);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject.getString("access_token");
    }

    /**
     * 获取二维码ticket
     *
     * @return
     * @throws Exception
     */
    public JSONObject getQrCode(Boolean bind) throws Exception {
        // 获取token开发者
        String accessToken = getAccessToken();
        String getQrCodeUrl = wxConfig.getQrCodeUrl().replace("TOKEN", accessToken);
        // 这里生成一个带参数的二维码，参数是scene_str
        String sceneStr = CheckWXTokenUtils.getRandomString(8);
        if(bind){
            // 获取当前登录的用户
            AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
            sceneStr = "bind_" + userRolesVo.getUid();
        }
        String json = "{\"expire_seconds\": 604800, \"action_name\": \"QR_STR_SCENE\"" + ", \"action_info\": {\"scene\": {\"scene_str\": \"" + sceneStr + "\"}}}";
        String result = HttpClientUtil.doPostJson(getQrCodeUrl, json);
        JSONObject jsonObject = JSONObject.parseObject(result);
        jsonObject.put("sceneStr", sceneStr);
            /*  ticket
                expire_seconds
                url
                sceneStr*/
        return jsonObject;
    }

    /**
     * 服务器校验及回调处理
     *
     * @param request
     * @return
     */
    public String checkSign(HttpServletRequest request) throws Exception {
        //获取微信请求参数
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        //参数排序 token 就要换成自己实际写的 token
        String[] params = new String[]{timestamp, nonce, wxConfig.getToken()};
        Arrays.sort(params);
        //拼接
        String paramstr = params[0] + params[1] + params[2];
        //加密
        //获取 shal 算法封装类
        MessageDigest Sha1Dtgest = MessageDigest.getInstance("SHA-1");
        //进行加密
        byte[] digestResult = Sha1Dtgest.digest(paramstr.getBytes("UTF-8"));
        //拿到加密结果
        String mysignature = CheckWXTokenUtils.toHexString(digestResult);
        mysignature = mysignature.toLowerCase(Locale.ROOT);
        //是否正确
        boolean signsuccess = mysignature.equals(signature);
        //逻辑处理
        if (signsuccess && echostr != null) {
            //验证签名，接入服务器
            return echostr;
        } else {
            //接入失败或已经接入成功后
            return callback(request);
        }
    }

    /**
     * 回调方法
     *
     * @param request
     * @return
     * @throws Exception
     */
    private String callback(HttpServletRequest request) throws Exception {
        // request中有相应的信息，进行解析
        // 获取消息流,并解析xml
        WxMpXmlMessage message = WxMpXmlMessage.fromXml(request.getInputStream());
        log.info("message为" + message.toString());
        // 消息类型
        String messageType = message.getMsgType();
        // 发送者帐号openid
        String fromUser = message.getFromUser();
        String event = message.getEvent();
        // 生成二维码时穿过的特殊参数
        String sceneStr =message.getEventKey();

        //if判断，判断查询
        if ("event".equals(messageType)) {

            // 先根据openid从数据库查询  => 从自己数据库中查取用户信息 => jsonObject
            UserRolesVO userRolesVo = userRoleEntityService.getUserRoles(null, null, fromUser);

            log.info("sceneStr放缓存"+sceneStr);
            if("subscribe".equals(event)){
                // 用户关注
                sceneStr = sceneStr.replaceAll("qrscene_","");
                redisUtils.set(sceneStr,fromUser,200);
                boolean dealWxBindResult = dealWxBind(userRolesVo,sceneStr,fromUser);
                if(dealWxBindResult){
                    return "success";
                }
                if (userRolesVo == null) {
                    // 没有注册过
                    // 发送授权请求，进行注册
                    String respMessage = textReplyManager.callback(message);
                    if (StringUtils.isBlank(respMessage)) {
                        log.info("不回复消息");
                    }
                    return respMessage;
                }
            }else if("SCAN".equals(event)){
                // 用户扫码
                redisUtils.set(sceneStr,fromUser,200);
                boolean dealWxBindResult = dealWxBind(userRolesVo,sceneStr,fromUser);
                if(dealWxBindResult){
                    return "success";
                }
                if (userRolesVo == null) {
                    // 没有注册过
                    // 发送授权请求，进行注册
                    String respMessage = textReplyManager.callback(message);
                    if (StringUtils.isBlank(respMessage)) {
                        log.info("不回复消息");
                    }
                    return respMessage;
                }
            }else if("LOCATION".equals(event)){
                // 地理位置，暂时不处理
            }

        }
        return "success";
    }

    /**
     * 处理微信绑定逻辑
     * @param userRolesVo
     * @param sceneStr
     * @param fromUser
     * @return
     * @throws StatusFailException
     */
    private boolean dealWxBind(UserRolesVO userRolesVo, String sceneStr, String fromUser) throws StatusFailException {

        if(sceneStr.startsWith("bind_")){
            if(userRolesVo != null){
                // 此微信号已经被绑定了
                return false;
            }
            // 获取绑定用户的uid
            String uid = sceneStr.replaceAll("bind_","");
            UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("uuid", uid)
                    .set("open_id", fromUser);
            userInfoEntityService.update(updateWrapper);
            // 绑定成功
            return true;
        }
        // 无需绑定
        return false;
    }

    public String oauthInvoke(HttpServletRequest request) throws Exception {
        // 获取code
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        log.info("code" + code);
        log.info("state" + state);

        // 通过code换取网页授权的accessToekn
        String getAccessToken = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + wxConfig.getAppId() +
                "&secret=" + wxConfig.getAppSecret() +
                "&code=" + code +
                "&grant_type=authorization_code";
        String result = HttpClientUtil.doGet(getAccessToken);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String accessToken = jsonObject.getString("access_token");
        String openId = jsonObject.getString("openid");
        log.info("网页授权获取到的accessToekn为：" + accessToken);

        // 获取用户详细信息
        String getUserInfo = "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=" + accessToken +
                "&openid=" + openId +
                "&lang=zh_CN";
        String userInfo = HttpClientUtil.doGet(getUserInfo);
        log.info("详细用户详细为：" + userInfo);
        JSONObject userInfoJsonObject = JSONObject.parseObject(userInfo);

        log.info("userInfoJsonObject为" + userInfoJsonObject);
        // 注册新用户
        // 数据库落库
        UserInfo wxUserInfo = new UserInfo();
        String uuid = IdUtil.simpleUUID();
        //为新用户设置uuid
        wxUserInfo.setUuid(uuid);
        wxUserInfo.setUsername(userInfoJsonObject.getString("openid"));
        wxUserInfo.setPassword("hqu123456");
        wxUserInfo.setNickname(userInfoJsonObject.getString("nickname"));
        String sex = userInfoJsonObject.getString("sex");
        wxUserInfo.setGender("0".equals(sex) ? "secrecy" : "1".equals(sex) ? "male" : "female");
        wxUserInfo.setOpenId(userInfoJsonObject.getString("openid"));
        wxUserInfo.setAvatar(userInfoJsonObject.getString("headimgurl"));
        log.info("UserInfo为" + wxUserInfo);


        //往user_info表插入数据
        boolean addUser = userInfoEntityService.addUser(wxUserInfo);

        //往user_role表插入数据
        boolean addUserRole = userRoleEntityService.save(new UserRole().setRoleId(1002L).setUid(uuid));

        //往user_record表插入数据
        boolean addUserRecord = userRecordEntityService.save(new UserRecord().setUid(uuid));

        if (addUser && addUserRole && addUserRecord) {
            noticeManager.syncNoticeToNewRegisterUser(uuid);
        } else {
            throw new StatusFailException("注册失败，请稍后重新尝试！");
        }
        return "注册登录成功！";
    }

    public UserInfoVO wxLogin(WxLoginDTO wxLoginDTO, HttpServletResponse response, HttpServletRequest request) throws StatusFailException {
        String userIpAddr = IpUtils.getUserIpAddr(request);
        String openId = (String) redisUtils.get(wxLoginDTO.getSceneStr());

        log.info("openId为：" + openId);
        log.info("SceneStr为：" + wxLoginDTO.getSceneStr());
        if(StringUtils.isEmpty(openId)){
            return null;
        }else{
//            redisUtils.del(wxLoginDTO.getSceneStr());
        }
        String key = Constants.Account.TRY_LOGIN_NUM.getCode() + openId + "_" + userIpAddr;
        // 获取登录次数
        Integer tryLoginCount = (Integer) redisUtils.get(key);

        if (tryLoginCount != null && tryLoginCount >= 20) {
            throw new StatusFailException("对不起！登录失败次数过多！您的账号有风险，半个小时内暂时无法登录！");
        }

        UserRolesVO userRolesVo = userRoleEntityService.getUserRoles(null, null, openId);

        if (userRolesVo == null) {
            throw new StatusFailException("用户不存在");
        }

        if (userRolesVo.getStatus() != 0) {
            throw new StatusFailException("该账户已被封禁，请联系管理员进行处理！");
        }

        String jwt = jwtUtils.generateToken(userRolesVo.getUid());
        response.setHeader("Authorization", jwt); //放到信息头部
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        // 会话记录
        sessionEntityService.save(new Session()
                .setUid(userRolesVo.getUid())
                .setIp(IpUtils.getUserIpAddr(request))
                .setUserAgent(request.getHeader("User-Agent")));

        // 登录成功，清除锁定限制
        if (tryLoginCount != null) {
            redisUtils.del(key);
        }

        // 异步检查是否异地登录
        sessionEntityService.checkRemoteLogin(userRolesVo.getUid());

        UserInfoVO userInfoVo = new UserInfoVO();
        BeanUtil.copyProperties(userRolesVo, userInfoVo, "roles");
        userInfoVo.setRoleList(userRolesVo.getRoles()
                .stream()
                .map(Role::getRole)
                .collect(Collectors.toList()));
        redisUtils.del(wxLoginDTO.getSceneStr());
        return userInfoVo;
    }

    public UserInfoVO checkWxBind(WxBindDTO wxBindDTO) throws StatusFailException {
        String uid = wxBindDTO.getSceneStr().replace("bind_","");
        UserRolesVO userRolesVo = userRoleEntityService.getUserRoles(uid, null, null);
        if(userRolesVo == null){
            throw new StatusFailException("绑定失败！");
        }
        UserInfoVO userInfoVo = new UserInfoVO();
        BeanUtil.copyProperties(userRolesVo, userInfoVo, "roles");
        userInfoVo.setRoleList(userRolesVo.getRoles()
                .stream()
                .map(Role::getRole)
                .collect(Collectors.toList()));
        log.info("userInfo为：" + userInfoVo.toString());
        return userInfoVo;
    }

    public UserInfoVO login(LoginDTO loginDto, HttpServletResponse response, HttpServletRequest request) throws StatusFailException {
        // 去掉账号密码首尾的空格
        loginDto.setPassword(loginDto.getPassword().trim());
        loginDto.setUsername(loginDto.getUsername().trim());

        if (StringUtils.isEmpty(loginDto.getUsername()) || StringUtils.isEmpty(loginDto.getPassword())) {
            throw new StatusFailException("用户名或密码不能为空！");
        }

        if (loginDto.getPassword().length() < 6 || loginDto.getPassword().length() > 20) {
            throw new StatusFailException("密码长度应该为6~20位！");
        }
        if (loginDto.getUsername().length() > 20) {
            throw new StatusFailException("用户名长度不能超过20位!");
        }

        String userIpAddr = IpUtils.getUserIpAddr(request);
        String key = Constants.Account.TRY_LOGIN_NUM.getCode() + loginDto.getUsername() + "_" + userIpAddr;
        Integer tryLoginCount = (Integer) redisUtils.get(key);

        if (tryLoginCount != null && tryLoginCount >= 20) {
            throw new StatusFailException("对不起！登录失败次数过多！您的账号有风险，半个小时内暂时无法登录！");
        }

        UserRolesVO userRolesVo = userRoleEntityService.getUserRoles(null, loginDto.getUsername(), null);

        if (userRolesVo == null) {
            throw new StatusFailException("用户名或密码错误！请注意大小写！");
        }

        if (!userRolesVo.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
            if (tryLoginCount == null) {
                redisUtils.set(key, 1, 60 * 30); // 三十分钟不尝试，该限制会自动清空消失
            } else {
                redisUtils.set(key, tryLoginCount + 1, 60 * 30);
            }
            throw new StatusFailException("用户名或密码错误！请注意大小写！");
        }

        if (userRolesVo.getStatus() != 0) {
            throw new StatusFailException("该账户已被封禁，请联系管理员进行处理！");
        }

        String jwt = jwtUtils.generateToken(userRolesVo.getUid());
        response.setHeader("Authorization", jwt); //放到信息头部
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        // 会话记录
        sessionEntityService.save(new Session()
                .setUid(userRolesVo.getUid())
                .setIp(IpUtils.getUserIpAddr(request))
                .setUserAgent(request.getHeader("User-Agent")));

        // 登录成功，清除锁定限制
        if (tryLoginCount != null) {
            redisUtils.del(key);
        }

        // 异步检查是否异地登录
        sessionEntityService.checkRemoteLogin(userRolesVo.getUid());

        UserInfoVO userInfoVo = new UserInfoVO();
        BeanUtil.copyProperties(userRolesVo, userInfoVo, "roles");
        userInfoVo.setRoleList(userRolesVo.getRoles()
                .stream()
                .map(Role::getRole)
                .collect(Collectors.toList()));
        return userInfoVo;
    }


    public RegisterCodeVO getRegisterCode(String email) throws StatusAccessDeniedException, StatusFailException, StatusForbiddenException {

        WebConfig webConfig = nacosSwitchConfig.getWebConfig();
        if (!webConfig.getRegister()) { // 需要判断一下网站是否开启注册
            throw new StatusAccessDeniedException("对不起！本站暂未开启注册功能！");
        }
        if (!emailManager.isOk()) {
            throw new StatusAccessDeniedException("对不起！本站邮箱系统未配置，暂不支持注册！");
        }

        email = email.trim();

        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) {
            throw new StatusFailException("对不起！您的邮箱格式不正确！");
        }

        boolean isBlackEmail = emailRuleBO.getBlackList().stream().anyMatch(email::endsWith);
        if (isBlackEmail) {
            throw new StatusForbiddenException("对不起！您的邮箱无法注册本网站！");
        }

        String lockKey = Constants.Email.REGISTER_EMAIL_LOCK + email;
        if (redisUtils.hasKey(lockKey)) {
            throw new StatusFailException("对不起，您的操作频率过快，请在" + redisUtils.getExpire(lockKey) + "秒后再次发送注册邮件！");
        }

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        UserInfo userInfo = userInfoEntityService.getOne(queryWrapper, false);
        if (userInfo != null) {
            throw new StatusFailException("对不起！该邮箱已被注册，请更换新的邮箱！");
        }

        String numbers = RandomUtil.randomNumbers(6); // 随机生成6位数字的组合
        redisUtils.set(Constants.Email.REGISTER_KEY_PREFIX.getValue() + email, numbers, 10 * 60);//默认验证码有效10分钟
        emailManager.sendRegisterCode(email, numbers);
        redisUtils.set(lockKey, 0, 60);

        RegisterCodeVO registerCodeVo = new RegisterCodeVO();
        registerCodeVo.setEmail(email);
        registerCodeVo.setExpire(5 * 60);

        return registerCodeVo;
    }


    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDto) throws StatusAccessDeniedException, StatusFailException {
        WebConfig webConfig = nacosSwitchConfig.getWebConfig();
        if (!webConfig.getRegister()) { // 需要判断一下网站是否开启注册
            throw new StatusAccessDeniedException("对不起！本站暂未开启注册功能！");
        }

        String codeKey = Constants.Email.REGISTER_KEY_PREFIX.getValue() + registerDto.getEmail();
        if (!redisUtils.hasKey(codeKey)) {
            throw new StatusFailException("验证码不存在或已过期");
        }

        if (!redisUtils.get(codeKey).equals(registerDto.getCode())) { //验证码判断
            throw new StatusFailException("验证码不正确");
        }

        if (StringUtils.isEmpty(registerDto.getPassword())) {
            throw new StatusFailException("密码不能为空");
        }

        if (registerDto.getPassword().length() < 6 || registerDto.getPassword().length() > 20) {
            throw new StatusFailException("密码长度应该为6~20位！");
        }

        if (StringUtils.isEmpty(registerDto.getUsername())) {
            throw new StatusFailException("用户名不能为空");
        }

        if (registerDto.getUsername().length() > 20) {
            throw new StatusFailException("用户名长度不能超过20位!");
        }

        String uuid = IdUtil.simpleUUID();
        //为新用户设置uuid
        registerDto.setUuid(uuid);
        registerDto.setPassword(SecureUtil.md5(registerDto.getPassword().trim())); // 将密码MD5加密写入数据库
        registerDto.setUsername(registerDto.getUsername().trim());
        registerDto.setEmail(registerDto.getEmail().trim());

        //往user_info表插入数据
        boolean addUser = userInfoEntityService.addUser(registerDto);

        //往user_role表插入数据
        boolean addUserRole = userRoleEntityService.save(new UserRole().setRoleId(1002L).setUid(uuid));

        //往user_record表插入数据
        boolean addUserRecord = userRecordEntityService.save(new UserRecord().setUid(uuid));

        if (addUser && addUserRole && addUserRecord) {
            redisUtils.del(registerDto.getEmail());
            noticeManager.syncNoticeToNewRegisterUser(uuid);
        } else {
            throw new StatusFailException("注册失败，请稍后重新尝试！");
        }
    }


    public void applyResetPassword(ApplyResetPasswordDTO applyResetPasswordDto) throws StatusFailException {

        String captcha = applyResetPasswordDto.getCaptcha();
        String captchaKey = applyResetPasswordDto.getCaptchaKey();
        String email = applyResetPasswordDto.getEmail();

        if (StringUtils.isEmpty(captcha) || StringUtils.isEmpty(email) || StringUtils.isEmpty(captchaKey)) {
            throw new StatusFailException("邮箱或验证码不能为空");
        }

        if (!emailManager.isOk()) {
            throw new StatusFailException("对不起！本站邮箱系统未配置，暂不支持重置密码！");
        }

        String lockKey = Constants.Email.RESET_EMAIL_LOCK + email;
        if (redisUtils.hasKey(lockKey)) {
            throw new StatusFailException("对不起，您的操作频率过快，请在" + redisUtils.getExpire(lockKey) + "秒后再次发送重置邮件！");
        }

        // 获取redis中的验证码
        String redisCode = (String) redisUtils.get(captchaKey);
        if (!redisCode.equals(captcha.trim().toLowerCase())) {
            throw new StatusFailException("验证码不正确");
        }

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("email", email.trim());
        UserInfo userInfo = userInfoEntityService.getOne(userInfoQueryWrapper, false);
        if (userInfo == null) {
            throw new StatusFailException("对不起，该邮箱无该用户，请重新检查！");
        }

        String code = IdUtil.simpleUUID().substring(0, 21); // 随机生成20位数字与字母的组合
        redisUtils.set(Constants.Email.RESET_PASSWORD_KEY_PREFIX.getValue() + userInfo.getUsername(), code, 10 * 60);//默认链接有效10分钟
        // 发送邮件
        emailManager.sendResetPassword(userInfo.getUsername(), code, email.trim());
        redisUtils.set(lockKey, 0, 90);
    }


    public void resetPassword(ResetPasswordDTO resetPasswordDto) throws StatusFailException {
        String username = resetPasswordDto.getUsername();
        String password = resetPasswordDto.getPassword();
        String code = resetPasswordDto.getCode();

        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(username) || StringUtils.isEmpty(code)) {
            throw new StatusFailException("用户名、新密码或验证码不能为空");
        }

        if (password.length() < 6 || password.length() > 20) {
            throw new StatusFailException("新密码长度应该为6~20位！");
        }

        String codeKey = Constants.Email.RESET_PASSWORD_KEY_PREFIX.getValue() + username;
        if (!redisUtils.hasKey(codeKey)) {
            throw new StatusFailException("重置密码链接不存在或已过期，请重新发送重置邮件");
        }

        if (!redisUtils.get(codeKey).equals(code)) { //验证码判断
            throw new StatusFailException("重置密码的验证码不正确，请重新输入");
        }

        UpdateWrapper<UserInfo> userInfoUpdateWrapper = new UpdateWrapper<>();
        userInfoUpdateWrapper.eq("username", username).set("password", SecureUtil.md5(password));
        boolean isOk = userInfoEntityService.update(userInfoUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("重置密码失败");
        }
        redisUtils.del(codeKey);
    }

    public void logout() {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        jwtUtils.cleanToken(userRolesVo.getUid());
        SecurityUtils.getSubject().logout();
    }
}