package cn.edu.hqu.databackup.dao.discussion;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.discussion.Reply;
import cn.edu.hqu.databackup.pojo.vo.ReplyVO;

import java.util.List;

/**
 * @Author: egret
 */
public interface ReplyEntityService extends IService<Reply> {

    public List<ReplyVO> getAllReplyByCommentId(Long cid, String uid, Boolean isRoot, Integer commentId);

    public void updateReplyMsg(Integer sourceId, String sourceType, String content,
                               Integer quoteId, String quoteType, String recipientId,String senderId);
}