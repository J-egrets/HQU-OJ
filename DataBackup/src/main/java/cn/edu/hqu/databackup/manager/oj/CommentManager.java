package cn.edu.hqu.databackup.manager.oj;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.emoji.EmojiUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.hqu.databackup.annotation.HOJAccessEnum;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.config.NacosSwitchConfig;
import cn.edu.hqu.databackup.config.SwitchConfig;
import cn.edu.hqu.databackup.dao.contest.ContestEntityService;
import cn.edu.hqu.databackup.dao.discussion.CommentEntityService;
import cn.edu.hqu.databackup.dao.discussion.CommentLikeEntityService;
import cn.edu.hqu.databackup.dao.discussion.DiscussionEntityService;
import cn.edu.hqu.databackup.dao.discussion.ReplyEntityService;
import cn.edu.hqu.databackup.dao.user.UserAcproblemEntityService;
import cn.edu.hqu.databackup.exception.AccessException;
import cn.edu.hqu.databackup.pojo.dto.ReplyDTO;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.discussion.Comment;
import cn.edu.hqu.api.pojo.entity.discussion.CommentLike;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.api.pojo.entity.discussion.Reply;
import cn.edu.hqu.api.pojo.entity.user.UserAcproblem;
import cn.edu.hqu.databackup.pojo.vo.CommentListVO;
import cn.edu.hqu.databackup.pojo.vo.CommentVO;
import cn.edu.hqu.databackup.pojo.vo.ReplyVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.validator.AccessValidator;
import cn.edu.hqu.databackup.validator.CommonValidator;
import cn.edu.hqu.databackup.validator.ContestValidator;
import cn.edu.hqu.databackup.validator.GroupValidator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: egret
 */
@Component
public class CommentManager {

    @Autowired
    private CommentEntityService commentEntityService;

    @Autowired
    private CommentLikeEntityService commentLikeEntityService;

    @Autowired
    private ReplyEntityService replyEntityService;

    @Autowired
    private DiscussionEntityService discussionEntityService;

    @Autowired
    private UserAcproblemEntityService userAcproblemEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private ContestValidator contestValidator;

    @Autowired
    private AccessValidator accessValidator;

    @Autowired
    private CommonValidator commonValidator;

    @Autowired
    private NacosSwitchConfig nacosSwitchConfig;

    private final static Pattern pattern = Pattern.compile("<.*?([a,A][u,U][t,T][o,O][p,P][l,L][a,A][y,Y]).*?>");

    public CommentListVO getComments(Long cid, Integer did, Integer limit, Integer currentPage) throws StatusForbiddenException, AccessException {

        // ????????????????????????????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        if (cid == null && did != null) {
            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", did);
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);
            if (discussion != null && discussion.getGid() != null) {
                accessValidator.validateAccess(HOJAccessEnum.GROUP_DISCUSSION);
                if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), discussion.getGid())) {
                    throw new StatusForbiddenException("?????????????????????????????????");
                }
            } else {
                accessValidator.validateAccess(HOJAccessEnum.PUBLIC_DISCUSSION);
            }
        } else {
            accessValidator.validateAccess(HOJAccessEnum.CONTEST_COMMENT);
        }

        IPage<CommentVO> commentList = commentEntityService.getCommentList(limit, currentPage, cid, did, isRoot,
                userRolesVo != null ? userRolesVo.getUid() : null);

        HashMap<Integer, Boolean> commentLikeMap = new HashMap<>();

        if (userRolesVo != null) {
            // ?????????????????? ????????????????????????????????????
            List<Integer> commentIdList = new LinkedList<>();

            for (CommentVO commentVo : commentList.getRecords()) {
                commentIdList.add(commentVo.getId());
            }

            if (commentIdList.size() > 0) {

                QueryWrapper<CommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
                commentLikeQueryWrapper.in("cid", commentIdList);

                List<CommentLike> commentLikeList = commentLikeEntityService.list(commentLikeQueryWrapper);

                // ??????????????????????????????Map???true
                for (CommentLike tmp : commentLikeList) {
                    commentLikeMap.put(tmp.getCid(), true);
                }
            }
        }

        CommentListVO commentListVo = new CommentListVO();
        commentListVo.setCommentList(commentList);
        commentListVo.setCommentLikeMap(commentLikeMap);
        return commentListVo;
    }


    @Transactional
    public CommentVO addComment(Comment comment) throws StatusFailException, StatusForbiddenException, AccessException {

        commonValidator.validateContent(comment.getContent(), "??????", 10000);

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Long cid = comment.getCid();

        // ?????????????????? ??????????????? ??????AC 10?????????????????????
        if (cid == null) {

            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);
            if (discussion == null) {
                throw new StatusFailException("???????????????????????????????????????");
            }
            Long gid = discussion.getGid();
            if (gid != null) {
                accessValidator.validateAccess(HOJAccessEnum.GROUP_DISCUSSION);
                if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("?????????????????????????????????");
                }
            } else {
                accessValidator.validateAccess(HOJAccessEnum.PUBLIC_DISCUSSION);
            }

            if (!isRoot && !isProblemAdmin && !isAdmin) {
                QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
                int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);

                SwitchConfig switchConfig = nacosSwitchConfig.getSwitchConfig();
                if (userAcProblemCount < switchConfig.getDefaultCreateCommentACInitValue()) {
                    throw new StatusForbiddenException("???????????????????????????????????????????????????????????????"
                            + switchConfig.getDefaultCreateCommentACInitValue() + "?????????!");
                }
            }

        } else {
            accessValidator.validateAccess(HOJAccessEnum.CONTEST_COMMENT);
            Contest contest = contestEntityService.getById(cid);
            if (contest == null) {
                throw new StatusFailException("???????????????????????????????????????");
            }
            contestValidator.validateContestAuth(contest, userRolesVo, isRoot);
        }

        comment.setFromAvatar(userRolesVo.getAvatar())
                .setFromName(userRolesVo.getUsername())
                .setFromUid(userRolesVo.getUid());

        if (SecurityUtils.getSubject().hasRole("root")) {
            comment.setFromRole("root");
        } else if (SecurityUtils.getSubject().hasRole("admin")
                || SecurityUtils.getSubject().hasRole("problem_admin")) {
            comment.setFromRole("admin");
        } else {
            comment.setFromRole("user");
        }

        // ???????????????????????????????????????
        comment.setContent(EmojiUtil.toHtml(formatContentRemoveAutoPlay(comment.getContent())));

        boolean isOk = commentEntityService.saveOrUpdate(comment);

        if (isOk) {
            CommentVO commentVo = new CommentVO();
            commentVo.setContent(comment.getContent());
            commentVo.setId(comment.getId());
            commentVo.setFromAvatar(comment.getFromAvatar());
            commentVo.setFromName(comment.getFromName());
            commentVo.setFromUid(comment.getFromUid());
            commentVo.setLikeNum(0);
            commentVo.setGmtCreate(comment.getGmtCreate());
            commentVo.setReplyList(new LinkedList<>());
            commentVo.setFromTitleName(userRolesVo.getTitleName());
            commentVo.setFromTitleColor(userRolesVo.getTitleColor());
            // ?????????????????????????????????????????????????????????????????????????????????
            if (comment.getDid() != null) {
                Discussion discussion = discussionEntityService.getById(comment.getDid());
                if (discussion != null) {
                    discussion.setCommentNum(discussion.getCommentNum() + 1);
                    discussionEntityService.updateById(discussion);
                    // ????????????
                    commentEntityService.updateCommentMsg(discussion.getUid(),
                            userRolesVo.getUid(),
                            comment.getContent(),
                            comment.getDid(),
                            discussion.getGid());
                }
            }
            return commentVo;
        } else {
            throw new StatusFailException("?????????????????????????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Comment comment) throws StatusForbiddenException, StatusFailException, AccessException {

        commonValidator.validateNotEmpty(comment.getId(), "??????ID");
        // ???????????????????????? ????????????????????? ????????????????????????
        comment = commentEntityService.getById(comment.getId());
        if (comment == null) {
            throw new StatusFailException("??????????????????????????????????????????");
        }

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Long cid = comment.getCid();
        if (cid == null) {
            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);
            Long gid = discussion.getGid();
            if (gid == null) {
                accessValidator.validateAccess(HOJAccessEnum.PUBLIC_DISCUSSION);
                if (!comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !isProblemAdmin && !isAdmin) {
                    throw new StatusForbiddenException("?????????????????????");
                }
            } else {
                accessValidator.validateAccess(HOJAccessEnum.GROUP_DISCUSSION);
                if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid)
                        && !comment.getFromUid().equals(userRolesVo.getUid())
                        && !isRoot) {
                    throw new StatusForbiddenException("?????????????????????");
                }
            }
        } else {
            accessValidator.validateAccess(HOJAccessEnum.CONTEST_COMMENT);
            Contest contest = contestEntityService.getById(cid);
            Long gid = contest.getGid();
            if (!comment.getFromUid().equals(userRolesVo.getUid())
                    && !isRoot
                    && !contest.getUid().equals(userRolesVo.getUid())
                    && !(contest.getIsGroup() && groupValidator.isGroupRoot(userRolesVo.getUid(), gid))) {
                throw new StatusForbiddenException("?????????????????????");
            }
        }
        // ???????????????????????????????????????
        int replyNum = replyEntityService.count(new QueryWrapper<Reply>().eq("comment_id", comment.getId()));

        // ??????????????? ?????????????????????reply?????????
        boolean isDeleteComment = commentEntityService.removeById(comment.getId());

        // ?????????????????????????????????????????????
        replyEntityService.remove(new UpdateWrapper<Reply>().eq("comment_id", comment.getId()));

        if (isDeleteComment) {
            // ?????????????????????????????????????????????????????????????????????????????????
            if (comment.getDid() != null) {
                UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
                discussionUpdateWrapper.eq("id", comment.getDid())
                        .setSql("comment_num=comment_num-" + (replyNum + 1));
                discussionEntityService.update(discussionUpdateWrapper);
            }
        } else {
            throw new StatusFailException("??????????????????????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addCommentLike(Integer cid, Boolean toLike, Integer sourceId, String sourceType) throws StatusFailException {

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        QueryWrapper<CommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
        commentLikeQueryWrapper.eq("cid", cid).eq("uid", userRolesVo.getUid());

        CommentLike commentLike = commentLikeEntityService.getOne(commentLikeQueryWrapper, false);

        if (toLike) { // ????????????
            if (commentLike == null) { // ????????????????????????
                boolean isSave = commentLikeEntityService.saveOrUpdate(new CommentLike()
                        .setUid(userRolesVo.getUid())
                        .setCid(cid));
                if (!isSave) {
                    throw new StatusFailException("?????????????????????????????????");
                }
            }
            // ??????+1
            Comment comment = commentEntityService.getById(cid);
            if (comment != null) {
                comment.setLikeNum(comment.getLikeNum() + 1);
                commentEntityService.updateById(comment);
                // ???????????????????????????????????? ?????????????????????
                if (!userRolesVo.getUsername().equals(comment.getFromName())) {
                    commentEntityService.updateCommentLikeMsg(comment.getFromUid(), userRolesVo.getUid(), sourceId, sourceType);
                }
            }
        } else { // ????????????
            if (commentLike != null) { // ?????????????????????
                boolean isDelete = commentLikeEntityService.removeById(commentLike.getId());
                if (!isDelete) {
                    throw new StatusFailException("???????????????????????????????????????");
                }
            }
            // ??????-1
            UpdateWrapper<Comment> commentUpdateWrapper = new UpdateWrapper<>();
            commentUpdateWrapper.setSql("like_num=like_num-1").eq("id", cid);
            commentEntityService.update(commentUpdateWrapper);
        }

    }

    public List<ReplyVO> getAllReply(Integer commentId, Long cid) throws StatusForbiddenException, StatusFailException, AccessException {

        // ????????????????????????????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        if (cid == null) {
            Comment comment = commentEntityService.getById(commentId);
            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);
            Long gid = discussion.getGid();
            if (gid != null) {
                accessValidator.validateAccess(HOJAccessEnum.GROUP_DISCUSSION);
                if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("?????????????????????????????????");
                }
            } else {
                accessValidator.validateAccess(HOJAccessEnum.PUBLIC_DISCUSSION);
            }
        } else {
            accessValidator.validateAccess(HOJAccessEnum.CONTEST_COMMENT);
            Contest contest = contestEntityService.getById(cid);
            contestValidator.validateContestAuth(contest, userRolesVo, isRoot);
        }

        return replyEntityService.getAllReplyByCommentId(cid,
                userRolesVo != null ? userRolesVo.getUid() : null,
                isRoot,
                commentId);
    }


    public ReplyVO addReply(ReplyDTO replyDto) throws StatusFailException, StatusForbiddenException, AccessException {

        commonValidator.validateContent(replyDto.getReply().getContent(), "??????", 10000);

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Reply reply = replyDto.getReply();

        if (reply == null || reply.getCommentId() == null){
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        Comment comment = commentEntityService.getById(reply.getCommentId());

        if (comment == null) {
            throw new StatusFailException("??????????????????????????????????????????");
        }

        Long cid = comment.getCid();
        if (cid == null) {

            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);

            Long gid = discussion.getGid();
            if (gid != null) {
                accessValidator.validateAccess(HOJAccessEnum.GROUP_DISCUSSION);
                if (!groupValidator.isGroupMember(userRolesVo.getUid(), gid) && !isRoot) {
                    throw new StatusForbiddenException("?????????????????????????????????");
                }
            } else {
                accessValidator.validateAccess(HOJAccessEnum.PUBLIC_DISCUSSION);
            }

            if (!isRoot && !isProblemAdmin && !isAdmin) {
                QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
                int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);
                SwitchConfig switchConfig = nacosSwitchConfig.getSwitchConfig();
                if (userAcProblemCount < switchConfig.getDefaultCreateCommentACInitValue()) {
                    throw new StatusForbiddenException("???????????????????????????????????????????????????????????????" +
                            switchConfig.getDefaultCreateCommentACInitValue() + "?????????!");
                }
            }
        } else {
            accessValidator.validateAccess(HOJAccessEnum.CONTEST_COMMENT);
            Contest contest = contestEntityService.getById(cid);
            Long gid = contest.getGid();
            if (!comment.getFromUid().equals(userRolesVo.getUid())
                    && !isRoot
                    && !contest.getUid().equals(userRolesVo.getUid())
                    && !(contest.getIsGroup() && groupValidator.isGroupRoot(userRolesVo.getUid(), gid))) {
                throw new StatusForbiddenException("?????????????????????????????????");
            }
        }
        reply.setFromAvatar(userRolesVo.getAvatar())
                .setFromName(userRolesVo.getUsername())
                .setFromUid(userRolesVo.getUid());

        if (SecurityUtils.getSubject().hasRole("root")) {
            reply.setFromRole("root");
        } else if (SecurityUtils.getSubject().hasRole("admin")
                || SecurityUtils.getSubject().hasRole("problem_admin")) {
            reply.setFromRole("admin");
        } else {
            reply.setFromRole("user");
        }
        // ???????????????????????????????????????
        reply.setContent(EmojiUtil.toHtml(formatContentRemoveAutoPlay(reply.getContent())));

        boolean isOk = replyEntityService.saveOrUpdate(reply);

        if (isOk) {
            // ?????????????????????????????????????????????????????????????????????????????????
            if (replyDto.getDid() != null) {
                UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
                discussionUpdateWrapper.eq("id", replyDto.getDid())
                        .setSql("comment_num=comment_num+1");
                discussionEntityService.update(discussionUpdateWrapper);
                // ????????????
                replyEntityService.updateReplyMsg(replyDto.getDid(),
                        "Discussion",
                        reply.getContent(),
                        replyDto.getQuoteId(),
                        replyDto.getQuoteType(),
                        reply.getToUid(),
                        reply.getFromUid());
            }

            ReplyVO replyVo = new ReplyVO();
            BeanUtil.copyProperties(reply, replyVo);
            replyVo.setFromTitleName(userRolesVo.getTitleName());
            replyVo.setFromTitleColor(userRolesVo.getTitleColor());
            return replyVo;
        } else {
            throw new StatusFailException("?????????????????????????????????");
        }
    }

    public void deleteReply(ReplyDTO replyDto) throws StatusForbiddenException, StatusFailException, AccessException {

        Reply reply = replyDto.getReply();

        if (reply == null || reply.getId() == null){
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        reply = replyEntityService.getById(reply.getId());

        if (reply == null) {
            throw new StatusFailException("???????????????????????????????????????????????????");
        }

        Comment comment = commentEntityService.getById(reply.getCommentId());
        if (comment == null) {
            throw new StatusFailException("?????????????????????????????????????????????????????????");
        }

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Long cid = comment.getCid();
        if (cid == null) {
            Discussion discussion = discussionEntityService.getById(comment.getDid());
            Long gid = discussion.getGid();
            if (gid == null) {
                accessValidator.validateAccess(HOJAccessEnum.PUBLIC_DISCUSSION);
                if (!reply.getFromUid().equals(userRolesVo.getUid())
                        && !isRoot
                        && !isProblemAdmin
                        && !isAdmin) {
                    throw new StatusForbiddenException("?????????????????????");
                }
            } else {
                accessValidator.validateAccess(HOJAccessEnum.GROUP_DISCUSSION);
                if (!reply.getFromUid().equals(userRolesVo.getUid())
                        && !isRoot
                        && !groupValidator.isGroupAdmin(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("?????????????????????");
                }
            }
        } else {
            accessValidator.validateAccess(HOJAccessEnum.CONTEST_COMMENT);
            Contest contest = contestEntityService.getById(cid);
            if (!reply.getFromUid().equals(userRolesVo.getUid())
                    && !isRoot
                    && !contest.getUid().equals(userRolesVo.getUid())
                    && !(contest.getIsGroup() && groupValidator.isGroupRoot(userRolesVo.getUid(), contest.getGid()))) {
                throw new StatusForbiddenException("?????????????????????");
            }
        }

        boolean isOk = replyEntityService.removeById(reply.getId());
        if (isOk) {
            // ?????????????????????????????????????????????????????????????????????????????????
            if (replyDto.getDid() != null) {
                UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
                discussionUpdateWrapper.eq("id", replyDto.getDid())
                        .setSql("comment_num=comment_num-1");
                discussionEntityService.update(discussionUpdateWrapper);
            }
        } else {
            throw new StatusFailException("??????????????????????????????");
        }
    }

    private String formatContentRemoveAutoPlay(String content) {
        StringBuilder sb = new StringBuilder(content);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            sb.replace(matcher.start(1), matcher.end(1), "controls");
        }
        return sb.toString();
    }

}