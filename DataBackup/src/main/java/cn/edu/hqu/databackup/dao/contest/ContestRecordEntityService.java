package cn.edu.hqu.databackup.dao.contest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.contest.ContestRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.databackup.pojo.vo.ContestRecordVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @Author: egret
 */
public interface ContestRecordEntityService extends IService<ContestRecord> {

    IPage<ContestRecord> getACInfo(Integer currentPage,
                                   Integer limit,
                                   Integer status,
                                   Long cid,
                                   String contestCreatorId);

    List<ContestRecordVO> getOIContestRecord(Contest contest, List<Integer> externalCidList, Boolean isOpenSealRank);

    List<ContestRecordVO> getACMContestRecord(String contestCreatorUid, Long cid, List<Integer> externalCidList, Date startTime);

}
