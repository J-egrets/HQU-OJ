package cn.edu.hqu.databackup.mapper;

import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.databackup.pojo.vo.ContestVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: egret
 */
@Mapper
@Repository
public interface GroupContestMapper extends BaseMapper<Contest> {

    List<ContestVO> getContestList(IPage iPage, @Param("gid") Long gid);

    List<Contest> getAdminContestList(IPage iPage, @Param("gid") Long gid);

}
