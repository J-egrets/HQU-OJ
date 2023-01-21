package cn.edu.hqu.databackup.mapper;

import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;
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
public interface GroupProblemMapper extends BaseMapper<Problem> {

    List<ProblemVO> getProblemList(IPage iPage, @Param("gid") Long gid);

    List<Problem> getAdminProblemList(IPage iPage, @Param("gid") Long gid);
}
