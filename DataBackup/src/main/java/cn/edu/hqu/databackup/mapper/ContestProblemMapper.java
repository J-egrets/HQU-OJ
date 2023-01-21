package cn.edu.hqu.databackup.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.contest.ContestProblem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.edu.hqu.databackup.pojo.vo.ContestProblemVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemFullScreenListVO;

import java.util.Date;
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
public interface ContestProblemMapper extends BaseMapper<ContestProblem> {
    List<ContestProblemVO> getContestProblemList(@Param("cid") Long cid, @Param("startTime") Date startTime,
                                                 @Param("endTime") Date endTime, @Param("sealTime") Date sealTime,
                                                 @Param("isAdmin") Boolean isAdmin, @Param("adminList") List<String> adminList);

    List<ProblemFullScreenListVO> getContestFullScreenProblemList(@Param("cid") Long cid);
}
