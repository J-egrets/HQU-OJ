package cn.edu.hqu.databackup.service.oj;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ReplyDTO;
import cn.edu.hqu.api.pojo.entity.discussion.Comment;
import cn.edu.hqu.databackup.pojo.vo.CommentListVO;
import cn.edu.hqu.databackup.pojo.vo.CommentVO;
import cn.edu.hqu.databackup.pojo.vo.ReplyVO;

import java.util.List;

/**
 * @author egret
 */
public interface CommentService {

    public CommonResult<CommentListVO> getComments(Long cid, Integer did, Integer limit, Integer currentPage);

    public CommonResult<CommentVO> addComment(Comment comment);

    public CommonResult<Void> deleteComment(Comment comment);

    public CommonResult<Void> addCommentLike(Integer cid, Boolean toLike, Integer sourceId, String sourceType);

    public CommonResult<List<ReplyVO>> getAllReply(Integer commentId, Long cid);

    public CommonResult<ReplyVO> addReply(ReplyDTO replyDto);

    public CommonResult<Void> deleteReply(ReplyDTO replyDto);
}
