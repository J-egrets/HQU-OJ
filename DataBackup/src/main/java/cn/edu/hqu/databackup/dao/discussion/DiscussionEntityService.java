package cn.edu.hqu.databackup.dao.discussion;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.databackup.pojo.vo.DiscussionVO;

/**
 * @author egret
 */
public interface DiscussionEntityService extends IService<Discussion> {

    DiscussionVO getDiscussion(Integer did, String uid);

    void updatePostLikeMsg(String recipientId, String senderId, Integer discussionId, Long gid);
}
