package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.problem.ProblemTag;

/**
 * @author egret
 */
@Mapper
@Repository
public interface ProblemTagMapper extends BaseMapper<ProblemTag> {
}
