package cn.edu.hqu.databackup.dao.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.databackup.pojo.vo.ACMRankVO;
import cn.edu.hqu.api.pojo.entity.user.UserRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.databackup.pojo.vo.OIRankVO;
import cn.edu.hqu.databackup.pojo.vo.UserHomeVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @Author: egret
 */
public interface UserRecordEntityService extends IService<UserRecord> {

    List<ACMRankVO> getRecent7ACRank();

    UserHomeVO getUserHomeInfo(String uid, String username);

    List<Judge> getLastYearUserJudgeList(String uid, String username);

    IPage<OIRankVO> getOIRankList(Page<OIRankVO> page, List<String> uidList);

    IPage<ACMRankVO> getACMRankList(Page<ACMRankVO> page, List<String> uidList);

    IPage<OIRankVO> getGroupRankList(Page<OIRankVO> page, Long gid, List<String> uidList, String rankType, Boolean useCache);

}
