package cn.edu.hqu.databackup.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.problem.ProblemCount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author: egret
 */
@Mapper
@Repository
public interface ProblemCountMapper extends BaseMapper<ProblemCount> {
    ProblemCount getContestProblemCount(@Param("pid") Long pid, @Param("cpid") Long cpid, @Param("cid") Long cid);
}
