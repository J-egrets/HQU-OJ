package cn.edu.hqu.databackup.dao.discussion.impl;

import cn.edu.hqu.databackup.dao.discussion.DiscussionEntityService;
import cn.edu.hqu.databackup.dao.group.GroupMemberEntityService;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.api.pojo.entity.group.GroupMember;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import cn.edu.hqu.databackup.mapper.CommentMapper;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.discussion.Comment;
import cn.edu.hqu.api.pojo.entity.msg.MsgRemind;
import cn.edu.hqu.databackup.pojo.vo.CommentVO;
import cn.edu.hqu.databackup.dao.contest.ContestEntityService;
import cn.edu.hqu.databackup.dao.discussion.CommentEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.dao.msg.MsgRemindEntityService;
import cn.edu.hqu.databackup.dao.user.UserInfoEntityService;
import cn.edu.hqu.databackup.utils.Constants;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @Author: egret
 */
@Service
public class CommentEntityServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentEntityService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussionEntityService discussionEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Resource
    private MsgRemindEntityService msgRemindEntityService;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    @Override
    public IPage<CommentVO> getCommentList(int limit, int currentPage, Long cid, Integer did, Boolean isRoot, String uid) {
        //新建分页
        Page<CommentVO> page = new Page<>(currentPage, limit);

        if (cid != null) {
            Contest contest = contestEntityService.getById(cid);

            boolean onlyMineAndAdmin = contest.getStatus().equals(Constants.Contest.STATUS_RUNNING.getCode())
                    && !isRoot && !contest.getUid().equals(uid);
            if (onlyMineAndAdmin) { // 自己和比赛管理者评论可看

                List<String> myAndAdminUidList = userInfoEntityService.getSuperAdminUidList();
                myAndAdminUidList.add(uid);
                myAndAdminUidList.add(contest.getUid());
                Long gid = contest.getGid();
                if (gid != null) {
                    QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
                    groupMemberQueryWrapper.eq("gid", gid).eq("auth", 5);
                    List<GroupMember> groupAdminUidList = groupMemberEntityService.list(groupMemberQueryWrapper);

                    for (GroupMember groupMember : groupAdminUidList) {
                        myAndAdminUidList.add(groupMember.getUid());
                    }
                }
                return commentMapper.getCommentList(page, cid, did, true, myAndAdminUidList);
            }

        }
        return commentMapper.getCommentList(page, cid, did, false, null);
    }

    @Async
    @Override
    public void updateCommentMsg(String recipientId, String senderId, String content, Integer discussionId, Long gid) {

        if (content.length() > 200) {
            content = content.substring(0, 200) + "...";
        }

        MsgRemind msgRemind = new MsgRemind();
        msgRemind.setAction("Discuss")
                .setRecipientId(recipientId)
                .setSenderId(senderId)
                .setSourceContent(content)
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


    @Async
    @Override
    public void updateCommentLikeMsg(String recipientId, String senderId, Integer sourceId, String sourceType) {

        MsgRemind msgRemind = new MsgRemind();
        msgRemind.setAction("Like_Discuss")
                .setRecipientId(recipientId)
                .setSenderId(senderId)
                .setSourceId(sourceId)
                .setSourceType(sourceType);

        if (sourceType.equals("Discussion")) {

            Discussion discussion = discussionEntityService.getById(sourceId);
            if (discussion != null) {
                if (discussion.getGid() != null) {
                    msgRemind.setUrl("/group/" + discussion.getGid() + "/discussion-detail/" + sourceId);
                } else {
                    msgRemind.setUrl("/discussion-detail/" + sourceId);
                }
            }
        } else if (sourceType.equals("Contest")) {
            msgRemind.setUrl("/contest/" + sourceId + "/comment");
        }

        msgRemindEntityService.saveOrUpdate(msgRemind);
    }
}