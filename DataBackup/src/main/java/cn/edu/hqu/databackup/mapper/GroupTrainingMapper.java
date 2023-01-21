package cn.edu.hqu.databackup.mapper;

import cn.edu.hqu.api.pojo.entity.training.Training;
import cn.edu.hqu.databackup.pojo.vo.TrainingVO;
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
public interface GroupTrainingMapper extends BaseMapper<Training> {

    List<TrainingVO> getTrainingList(IPage iPage, @Param("gid") Long gid);

    List<Training> getAdminTrainingList(IPage iPage, @Param("gid") Long gid);

}
