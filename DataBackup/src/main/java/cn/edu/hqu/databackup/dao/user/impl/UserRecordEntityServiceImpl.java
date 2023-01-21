package cn.edu.hqu.databackup.dao.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import cn.edu.hqu.databackup.mapper.JudgeMapper;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.databackup.pojo.vo.ACMRankVO;
import cn.edu.hqu.api.pojo.entity.user.UserRecord;
import cn.edu.hqu.databackup.mapper.UserRecordMapper;
import cn.edu.hqu.databackup.pojo.vo.OIRankVO;
import cn.edu.hqu.databackup.pojo.vo.UserHomeVO;
import cn.edu.hqu.databackup.dao.user.UserRecordEntityService;
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
public class UserRecordEntityServiceImpl extends ServiceImpl<UserRecordMapper, UserRecord> implements UserRecordEntityService {

    @Autowired
    private UserRecordMapper userRecordMapper;

    @Autowired
    private JudgeMapper judgeMapper;

    @Autowired
    private RedisUtils redisUtils;

    // 排行榜缓存时间 10s
    private static final long cacheRankSecond = 10;

    @Override
    public List<ACMRankVO> getRecent7ACRank() {
        return userRecordMapper.getRecent7ACRank();
    }

    @Override
    public UserHomeVO getUserHomeInfo(String uid, String username) {
        return userRecordMapper.getUserHomeInfo(uid, username);
    }

    @Override
    public List<Judge> getLastYearUserJudgeList(String uid, String username) {
        return judgeMapper.getLastYearUserJudgeList(uid, username);
    }

    @Override
    public IPage<OIRankVO> getOIRankList(Page<OIRankVO> page, List<String> uidList) {
        return userRecordMapper.getOIRankList(page, uidList);
    }

    @Override
    public IPage<ACMRankVO> getACMRankList(Page<ACMRankVO> page, List<String> uidList) {
        return userRecordMapper.getACMRankList(page, uidList);
    }

    @Override
    public IPage<OIRankVO> getGroupRankList(Page<OIRankVO> page, Long gid, List<String> uidList, String rankType, Boolean useCache) {
        if (useCache) {
            IPage<OIRankVO> data = null;
            String key = Constants.Account.GROUP_RANK_CACHE.getCode() + "_" + gid + "_" + rankType + "_" + page.getCurrent() + "_" + page.getSize();
            data = (IPage<OIRankVO>) redisUtils.get(key);
            if (data == null) {
                data = userRecordMapper.getGroupRankList(page, gid, uidList, rankType);
                redisUtils.set(key, data, cacheRankSecond);
            }
            return data;
        } else {
            return userRecordMapper.getGroupRankList(page, gid, uidList, rankType);
        }
    }
}
