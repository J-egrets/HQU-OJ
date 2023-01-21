package cn.edu.hqu.databackup.dao.user;

import cn.edu.hqu.databackup.pojo.dto.RegisterDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.user.UserInfo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @Author: egret
 */
public interface UserInfoEntityService extends IService<UserInfo> {

    public Boolean addUser(RegisterDTO registerDto);

    public List<String> getSuperAdminUidList();

    public List<String> getProblemAdminUidList();
}
