package cn.edu.hqu.judgeserver.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.user.UserRecord;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author egret
 */
public interface UserRecordEntityService extends IService<UserRecord> {
    void updateRecord(String uid, Long submitId, Long pid, Integer score);
}
