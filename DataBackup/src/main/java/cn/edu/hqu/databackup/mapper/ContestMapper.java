package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.databackup.pojo.vo.ContestRegisterCountVO;
import cn.edu.hqu.databackup.pojo.vo.ContestVO;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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
public interface ContestMapper extends BaseMapper<Contest> {

    List<ContestVO> getContestList(IPage page,
                                   @Param("type") Integer type,
                                   @Param("status") Integer status,
                                   @Param("keyword") String keyword);

    List<ContestRegisterCountVO> getContestRegisterCount(@Param("cidList") List<Long> cidList);

    ContestVO getContestInfoById(@Param("cid") long cid);

    List<ContestVO> getWithinNext14DaysContests();
}
