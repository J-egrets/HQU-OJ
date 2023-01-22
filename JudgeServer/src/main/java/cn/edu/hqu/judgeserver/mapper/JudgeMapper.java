package cn.edu.hqu.judgeserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.judge.Judge;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author egret
 */
@Mapper
@Repository
public interface JudgeMapper extends BaseMapper<Judge> {

}