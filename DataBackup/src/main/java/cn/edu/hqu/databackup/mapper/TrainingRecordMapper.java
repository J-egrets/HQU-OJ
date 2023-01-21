package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.training.TrainingRecord;
import cn.edu.hqu.databackup.pojo.vo.TrainingRecordVO;

import java.util.List;

/**
 * @Author: egret
 */

@Mapper
@Repository
public interface TrainingRecordMapper extends BaseMapper<TrainingRecord> {

    public List<TrainingRecordVO> getTrainingRecord(@Param("tid") Long tid);
}