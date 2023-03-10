package cn.edu.hqu.databackup.manager.msg;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.dao.contest.ContestEntityService;
import cn.edu.hqu.databackup.dao.discussion.CommentEntityService;
import cn.edu.hqu.databackup.dao.discussion.DiscussionEntityService;
import cn.edu.hqu.databackup.dao.discussion.ReplyEntityService;
import cn.edu.hqu.databackup.dao.msg.MsgRemindEntityService;
import cn.edu.hqu.databackup.dao.msg.UserSysNoticeEntityService;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.discussion.Comment;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.api.pojo.entity.discussion.Reply;
import cn.edu.hqu.api.pojo.entity.msg.MsgRemind;
import cn.edu.hqu.api.pojo.entity.msg.UserSysNotice;
import cn.edu.hqu.databackup.pojo.vo.UserMsgVO;
import cn.edu.hqu.databackup.pojo.vo.UserUnreadMsgCountVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Component
public class UserMessageManager {

    @Resource
    private MsgRemindEntityService msgRemindEntityService;

    @Resource
    private ContestEntityService contestEntityService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private DiscussionEntityService discussionEntityService;

    @Resource
    private CommentEntityService commentEntityService;

    @Resource
    private ReplyEntityService replyEntityService;

    @Resource
    private UserSysNoticeEntityService userSysNoticeEntityService;

    public UserUnreadMsgCountVO getUnreadMsgCount() {
        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        UserUnreadMsgCountVO userUnreadMsgCount = msgRemindEntityService.getUserUnreadMsgCount(userRolesVo.getUid());
        if (userUnreadMsgCount == null) {
            userUnreadMsgCount = new UserUnreadMsgCountVO(0, 0, 0, 0, 0);
        }
        return userUnreadMsgCount;
    }


    public void cleanMsg(String type, Long id) throws StatusFailException {

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        boolean isOk = cleanMsgByType(type, id, userRolesVo.getUid());
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }


    public IPage<UserMsgVO> getCommentMsg(Integer limit, Integer currentPage) {

        // ????????????????????????????????????????????????
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 5;
        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        return getUserMsgList(userRolesVo.getUid(), "Discuss", limit, currentPage);
    }


    public IPage<UserMsgVO> getReplyMsg(Integer limit, Integer currentPage) {

        // ????????????????????????????????????????????????
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 5;

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        return getUserMsgList(userRolesVo.getUid(), "Reply", limit, currentPage);
    }


    public IPage<UserMsgVO> getLikeMsg(Integer limit, Integer currentPage) {

        // ????????????????????????????????????????????????
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 5;

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        return getUserMsgList(userRolesVo.getUid(), "Like", limit, currentPage);
    }


    private boolean cleanMsgByType(String type, Long id, String uid) {

        switch (type) {
            case "Like":
            case "Discuss":
            case "Reply":
                UpdateWrapper<MsgRemind> updateWrapper1 = new UpdateWrapper<>();
                updateWrapper1
                        .eq(id != null, "id", id)
                        .eq("recipient_id", uid);
                return msgRemindEntityService.remove(updateWrapper1);
            case "Sys":
            case "Mine":
                UpdateWrapper<UserSysNotice> updateWrapper2 = new UpdateWrapper<>();
                updateWrapper2
                        .eq(id != null, "id", id)
                        .eq("recipient_id", uid);
                return userSysNoticeEntityService.remove(updateWrapper2);
        }
        return false;
    }


    private IPage<UserMsgVO> getUserMsgList(String uid, String action, int limit, int currentPage) {
        Page<UserMsgVO> page = new Page<>(currentPage, limit);
        IPage<UserMsgVO> userMsgList = msgRemindEntityService.getUserMsg(page, uid, action);
        if (userMsgList.getTotal() > 0) {
            switch (action) {
                case "Discuss":  // ????????????
                    return getUserDiscussMsgList(userMsgList);
                case "Reply": // ????????????
                    return getUserReplyMsgList(userMsgList);
                case "Like":
                    return getUserLikeMsgList(userMsgList);
                default:
                    throw new RuntimeException("invalid action:" + action);
            }
        } else {
            return userMsgList;
        }
    }


    private IPage<UserMsgVO> getUserDiscussMsgList(IPage<UserMsgVO> userMsgList) {

        List<Integer> discussionIds = userMsgList.getRecords()
                .stream()
                .map(UserMsgVO::getSourceId)
                .collect(Collectors.toList());
        Collection<Discussion> discussions = discussionEntityService.listByIds(discussionIds);
        for (Discussion discussion : discussions) {
            for (UserMsgVO userMsgVo : userMsgList.getRecords()) {
                if (Objects.equals(discussion.getId(), userMsgVo.getSourceId())) {
                    userMsgVo.setSourceTitle(discussion.getTitle());
                    break;
                }
            }
        }
        applicationContext.getBean(UserMessageManager.class).updateUserMsgRead(userMsgList);
        return userMsgList;
    }

    private IPage<UserMsgVO> getUserReplyMsgList(IPage<UserMsgVO> userMsgList) {

        for (UserMsgVO userMsgVo : userMsgList.getRecords()) {
            if ("Discussion".equals(userMsgVo.getSourceType())) {
                Discussion discussion = discussionEntityService.getById(userMsgVo.getSourceId());
                if (discussion != null) {
                    userMsgVo.setSourceTitle(discussion.getTitle());
                } else {
                    userMsgVo.setSourceTitle("????????????????????????!???The original discussion post has been deleted!???");
                }
            } else if ("Contest".equals(userMsgVo.getSourceType())) {
                Contest contest = contestEntityService.getById(userMsgVo.getSourceId());
                if (contest != null) {
                    userMsgVo.setSourceTitle(contest.getTitle());
                } else {
                    userMsgVo.setSourceTitle("?????????????????????!???The original contest has been deleted!???");
                }
            }

            if ("Comment".equals(userMsgVo.getQuoteType())) {
                Comment comment = commentEntityService.getById(userMsgVo.getQuoteId());
                if (comment != null) {
                    String content;
                    if (comment.getContent().length() < 100) {
                        content = comment.getFromName() + " : "
                                + comment.getContent();

                    } else {
                        content = comment.getFromName() + " : "
                                + comment.getContent().substring(0, 100) + "...";
                    }
                    userMsgVo.setQuoteContent(content);
                } else {
                    userMsgVo.setQuoteContent("???????????????????????????????????????Your original comments have been deleted!???");
                }

            } else if ("Reply".equals(userMsgVo.getQuoteType())) {
                Reply reply = replyEntityService.getById(userMsgVo.getQuoteId());
                if (reply != null) {
                    String content;
                    if (reply.getContent().length() < 100) {
                        content = reply.getFromName() + " : @" + reply.getToName() + "???"
                                + reply.getContent();

                    } else {
                        content = reply.getFromName() + " : @" + reply.getToName() + "???"
                                + reply.getContent().substring(0, 100) + "...";
                    }
                    userMsgVo.setQuoteContent(content);
                } else {
                    userMsgVo.setQuoteContent("???????????????????????????????????????Your original reply has been deleted!???");
                }
            }

        }

        applicationContext.getBean(UserMessageManager.class).updateUserMsgRead(userMsgList);
        return userMsgList;
    }

    private IPage<UserMsgVO> getUserLikeMsgList(IPage<UserMsgVO> userMsgList) {
        for (UserMsgVO userMsgVo : userMsgList.getRecords()) {
            if ("Discussion".equals(userMsgVo.getSourceType())) {
                Discussion discussion = discussionEntityService.getById(userMsgVo.getSourceId());
                if (discussion != null) {
                    userMsgVo.setSourceTitle(discussion.getTitle());
                } else {
                    userMsgVo.setSourceTitle("????????????????????????!???The original discussion post has been deleted!???");
                }
            } else if ("Contest".equals(userMsgVo.getSourceType())) {
                Contest contest = contestEntityService.getById(userMsgVo.getSourceId());
                if (contest != null) {
                    userMsgVo.setSourceTitle(contest.getTitle());
                } else {
                    userMsgVo.setSourceTitle("?????????????????????!???The original contest has been deleted!???");
                }
            }
        }
        applicationContext.getBean(UserMessageManager.class).updateUserMsgRead(userMsgList);
        return userMsgList;
    }


    @Async
    public void updateUserMsgRead(IPage<UserMsgVO> userMsgList) {
        List<Long> idList = userMsgList.getRecords().stream()
                .filter(userMsgVo -> !userMsgVo.getState())
                .map(UserMsgVO::getId)
                .collect(Collectors.toList());
        if (idList.size() == 0) {
            return;
        }
        UpdateWrapper<MsgRemind> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idList)
                .set("state", true);
        msgRemindEntityService.update(null, updateWrapper);
    }

}