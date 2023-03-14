package cn.edu.hqu.databackup.dao.user.impl;

import cn.edu.hqu.api.pojo.entity.contest.ContestRecord;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import cn.edu.hqu.databackup.pojo.dto.RegisterDTO;
import cn.edu.hqu.api.pojo.entity.user.UserInfo;
import cn.edu.hqu.databackup.mapper.UserInfoMapper;
import cn.edu.hqu.databackup.dao.user.UserInfoEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.utils.RedisUtils;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @Author: egret
 */
@Service
public class UserInfoEntityServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoEntityService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Boolean addUser(RegisterDTO registerDto) {
        return userInfoMapper.addUser(registerDto) == 1;
    }

    @Override
    public Boolean addUser(UserInfo wxUserInfo) {
        return userInfoMapper.addWxUser(wxUserInfo) == 1;
    }

    @Override
    public List<String> getSuperAdminUidList() {

        String cacheKey = Constants.Account.SUPER_ADMIN_UID_LIST_CACHE.getCode();
        List<String> superAdminUidList = (List<String>) redisUtils.get(cacheKey);
        if (superAdminUidList == null) {
            superAdminUidList = userInfoMapper.getSuperAdminUidList();
            redisUtils.set(cacheKey, superAdminUidList, 12 * 3600);
        }
        return superAdminUidList;
    }

    @Override
    public List<String> getProblemAdminUidList() {
        return userInfoMapper.getProblemAdminUidList();
    }

    @Override
    public UserInfo getUserInfo(String uid) {
        return userInfoMapper.getUserInfo(uid);
    }

}
