package cn.edu.hqu.databackup.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.problem.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @Author: egret
 */
@Mapper
@Repository
public interface TagMapper extends BaseMapper<Tag> {

}
