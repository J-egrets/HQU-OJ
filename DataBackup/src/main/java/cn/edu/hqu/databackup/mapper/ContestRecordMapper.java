package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.contest.ContestRecord;
import cn.edu.hqu.databackup.pojo.vo.ContestRecordVO;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author: egret
 */
@Mapper
@Repository
public interface ContestRecordMapper extends BaseMapper<ContestRecord> {
    List<ContestRecord> getACInfo(@Param("status") Integer status, @Param("cid") Long cid);

    List<ContestRecordVO> getOIContestRecordByRecentSubmission(@Param("cid") Long cid,
                                                               @Param("externalCidList") List<Integer> externalCidList,
                                                               @Param("contestCreatorUid") String contestCreatorUid,
                                                               @Param("isOpenSealRank") Boolean isOpenSealRank,
                                                               @Param("sealTime") Long sealTime,
                                                               @Param("endTime") Long endTime);

    List<ContestRecordVO> getOIContestRecordByHighestSubmission(@Param("cid") Long cid,
                                                                @Param("externalCidList") List<Integer> externalCidList,
                                                                @Param("contestCreatorUid") String contestCreatorUid,
                                                                @Param("isOpenSealRank") Boolean isOpenSealRank,
                                                                @Param("sealTime") Long sealTime,
                                                                @Param("endTime") Long endTime);

    List<ContestRecordVO> getACMContestRecord(@Param("contestCreatorUid") String contestCreatorUid,
                                              @Param("cid") Long cid,
                                              @Param("externalCidList") List<Integer> externalCidList,
                                              @Param("time") Long time);
}
