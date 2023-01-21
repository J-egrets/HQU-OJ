package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.discussion.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.edu.hqu.databackup.pojo.vo.CommentVO;

import java.util.List;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author: egret
 */
@Mapper
@Repository
public interface CommentMapper extends BaseMapper<Comment> {

    IPage<CommentVO> getCommentList(Page<CommentVO> page,
                                    @Param("cid") Long cid,
                                    @Param("did") Integer did,
                                    @Param("onlyMineAndAdmin") Boolean onlyMineAndAdmin,
                                    @Param("myAndAdminUidList") List<String> myAndAdminUidList);
}
