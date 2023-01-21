package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.discussion.Reply;
import cn.edu.hqu.databackup.pojo.vo.ReplyVO;

import java.util.List;

/**
 * @Author: egret
 */

@Mapper
@Repository
public interface ReplyMapper extends BaseMapper<Reply> {

    public List<ReplyVO> getAllReplyByCommentId(@Param("commentId") Integer commentId,
                                                @Param("myAndAdminUidList") List<String> myAndAdminUidList);
}