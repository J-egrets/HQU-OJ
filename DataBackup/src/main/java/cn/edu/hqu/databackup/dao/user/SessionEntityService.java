package cn.edu.hqu.databackup.dao.user;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.user.Session;

/**
 * @author egret
 */
public interface SessionEntityService extends IService<Session> {

    public void checkRemoteLogin(String uid);

}
