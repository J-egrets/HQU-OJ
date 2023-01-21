package cn.edu.hqu.databackup.mapper;

import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author: egret
 */
@Mapper
@Repository
public interface GroupDiscussionMapper extends BaseMapper<Discussion> {

}
