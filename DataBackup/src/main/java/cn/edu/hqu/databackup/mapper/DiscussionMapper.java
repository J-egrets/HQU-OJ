package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.databackup.pojo.vo.DiscussionVO;


/**
 * @author egret
 */
@Mapper
@Repository
public interface DiscussionMapper extends BaseMapper<Discussion> {
    DiscussionVO getDiscussion(@Param("did") Integer did, @Param("uid") String uid);
}
