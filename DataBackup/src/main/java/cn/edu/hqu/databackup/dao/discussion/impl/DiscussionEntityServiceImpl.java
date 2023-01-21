package cn.edu.hqu.databackup.dao.discussion.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.DiscussionMapper;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.api.pojo.entity.msg.MsgRemind;
import cn.edu.hqu.databackup.pojo.vo.DiscussionVO;
import cn.edu.hqu.databackup.dao.discussion.DiscussionEntityService;
import cn.edu.hqu.databackup.dao.msg.MsgRemindEntityService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class DiscussionEntityServiceImpl extends ServiceImpl<DiscussionMapper, Discussion> implements DiscussionEntityService {

    @Autowired
    private DiscussionMapper discussionMapper;

    @Override
    public DiscussionVO getDiscussion(Integer did, String uid) {
        return discussionMapper.getDiscussion(did, uid);
    }

    @Resource
    private MsgRemindEntityService msgRemindEntityService;

    @Async
    public void updatePostLikeMsg(String recipientId, String senderId, Integer discussionId, Long gid) {

        MsgRemind msgRemind = new MsgRemind();
        msgRemind.setAction("Like_Post")
                .setRecipientId(recipientId)
                .setSenderId(senderId)
                .setSourceId(discussionId)
                .setSourceType("Discussion")
                .setUrl("/discussion-detail/" + discussionId);

        if (gid != null) {
            msgRemind.setUrl("/group/" + gid + "/discussion-detail/" + discussionId);
        } else {
            msgRemind.setUrl("/discussion-detail/" + discussionId);
        }

        msgRemindEntityService.saveOrUpdate(msgRemind);
    }
}