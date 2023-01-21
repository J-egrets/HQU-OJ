package cn.edu.hqu.databackup.controller.oj;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.edu.hqu.databackup.annotation.AnonApi;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ReplyDTO;
import cn.edu.hqu.api.pojo.entity.discussion.Comment;
import cn.edu.hqu.databackup.pojo.vo.CommentListVO;
import cn.edu.hqu.databackup.pojo.vo.CommentVO;
import cn.edu.hqu.databackup.pojo.vo.ReplyVO;
import cn.edu.hqu.databackup.service.oj.CommentService;

import java.util.List;

/**
 * @Author: egret
 * @Description:
 */
@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;


    @GetMapping("/comments")
    @AnonApi
    public CommonResult<CommentListVO> getComments(@RequestParam(value = "cid", required = false) Long cid,
                                                   @RequestParam(value = "did", required = false) Integer did,
                                                   @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                                   @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage) {
        return commentService.getComments(cid, did, limit, currentPage);
    }


    @PostMapping("/comment")
    @RequiresPermissions("comment_add")
    @RequiresAuthentication
    public CommonResult<CommentVO> addComment(@RequestBody Comment comment) {
        return commentService.addComment(comment);
    }

    @DeleteMapping("/comment")
    @RequiresAuthentication
    public CommonResult<Void> deleteComment(@RequestBody Comment comment) {
        return commentService.deleteComment(comment);
    }

    @GetMapping("/comment-like")
    @RequiresAuthentication
    public CommonResult<Void> addCommentLike(@RequestParam("cid") Integer cid,
                                                @RequestParam("toLike") Boolean toLike,
                                                @RequestParam("sourceId") Integer sourceId,
                                                @RequestParam("sourceType") String sourceType) {
        return commentService.addCommentLike(cid, toLike, sourceId, sourceType);
    }

    @GetMapping("/reply")
    @AnonApi
    public CommonResult<List<ReplyVO>> getAllReply(@RequestParam("commentId") Integer commentId,
                                                   @RequestParam(value = "cid", required = false) Long cid) {
        return commentService.getAllReply(commentId, cid);
    }

    @PostMapping("/reply")
    @RequiresPermissions("reply_add")
    @RequiresAuthentication
    public CommonResult<ReplyVO> addReply(@RequestBody ReplyDTO replyDto) {
        return commentService.addReply(replyDto);
    }

    @DeleteMapping("/reply")
    @RequiresAuthentication
    public CommonResult<Void> deleteReply(@RequestBody ReplyDTO replyDto) {
        return commentService.deleteReply(replyDto);
    }

}
