package cn.edu.hqu.databackup.service.oj.impl;

import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.exception.AccessException;
import cn.edu.hqu.databackup.manager.oj.CommentManager;
import cn.edu.hqu.databackup.pojo.dto.ReplyDTO;
import cn.edu.hqu.api.pojo.entity.discussion.Comment;
import cn.edu.hqu.databackup.pojo.vo.CommentListVO;
import cn.edu.hqu.databackup.pojo.vo.CommentVO;
import cn.edu.hqu.databackup.pojo.vo.ReplyVO;
import cn.edu.hqu.databackup.service.oj.CommentService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: egret
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentManager commentManager;

    @Override
    public CommonResult<CommentListVO> getComments(Long cid, Integer did, Integer limit, Integer currentPage) {
        try {
            return CommonResult.successResponse(commentManager.getComments(cid, did, limit, currentPage));
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<CommentVO> addComment(Comment comment) {
        try {
            return CommonResult.successResponse(commentManager.addComment(comment));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> deleteComment(Comment comment) {
        try {
            commentManager.deleteComment(comment);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> addCommentLike(Integer cid, Boolean toLike, Integer sourceId, String sourceType) {
        try {
            commentManager.addCommentLike(cid, toLike, sourceId, sourceType);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<List<ReplyVO>> getAllReply(Integer commentId, Long cid) {
        try {
            return CommonResult.successResponse(commentManager.getAllReply(commentId, cid));
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

    @Override
    public CommonResult<ReplyVO> addReply(ReplyDTO replyDto) {
        try {
            return CommonResult.successResponse(commentManager.addReply(replyDto));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> deleteReply(ReplyDTO replyDto) {
        try {
            commentManager.deleteReply(replyDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }
}