package cn.edu.hqu.judgeserver.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.contest.ContestRecord;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author egret
 */
public interface ContestRecordEntityService extends IService<ContestRecord> {
    void updateContestRecord(Integer score, Integer status, Long submitId, Integer useTime);
}
