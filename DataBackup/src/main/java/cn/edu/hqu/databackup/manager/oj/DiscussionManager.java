package cn.edu.hqu.databackup.manager.oj;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.annotation.HOJAccessEnum;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.config.NacosSwitchConfig;
import cn.edu.hqu.databackup.config.SwitchConfig;
import cn.edu.hqu.databackup.dao.discussion.DiscussionEntityService;
import cn.edu.hqu.databackup.dao.discussion.DiscussionLikeEntityService;
import cn.edu.hqu.databackup.dao.discussion.DiscussionReportEntityService;
import cn.edu.hqu.databackup.dao.problem.CategoryEntityService;
import cn.edu.hqu.databackup.dao.problem.ProblemEntityService;
import cn.edu.hqu.databackup.dao.user.UserAcproblemEntityService;
import cn.edu.hqu.databackup.exception.AccessException;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.api.pojo.entity.discussion.DiscussionLike;
import cn.edu.hqu.api.pojo.entity.discussion.DiscussionReport;
import cn.edu.hqu.api.pojo.entity.problem.Category;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.api.pojo.entity.user.UserAcproblem;
import cn.edu.hqu.databackup.pojo.vo.DiscussionVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.utils.RedisUtils;
import cn.edu.hqu.databackup.validator.AccessValidator;
import cn.edu.hqu.databackup.validator.CommonValidator;
import cn.edu.hqu.databackup.validator.GroupValidator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Component
public class DiscussionManager {
    @Autowired
    private DiscussionEntityService discussionEntityService;

    @Autowired
    private DiscussionLikeEntityService discussionLikeEntityService;

    @Autowired
    private CategoryEntityService categoryEntityService;

    @Autowired
    private DiscussionReportEntityService discussionReportEntityService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserAcproblemEntityService userAcproblemEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private AccessValidator accessValidator;

    @Autowired
    private NacosSwitchConfig nacosSwitchConfig;

    @Autowired
    private CommonValidator commonValidator;

    public IPage<Discussion> getDiscussionList(Integer limit,
                                               Integer currentPage,
                                               Integer categoryId,
                                               String pid,
                                               boolean onlyMine,
                                               String keyword,
                                               boolean admin) {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();

        IPage<Discussion> iPage = new Page<>(currentPage, limit);

        if (categoryId != null) {
            discussionQueryWrapper.eq("category_id", categoryId);
        }

        if (!StringUtils.isEmpty(keyword)) {

            final String key = keyword.trim();

            discussionQueryWrapper.and(wrapper -> wrapper.like("title", key).or()
                    .like("author", key).or()
                    .like("id", key).or()
                    .like("description", key));
        }

        boolean isAdmin = SecurityUtils.getSubject().hasRole("root")
                || SecurityUtils.getSubject().hasRole("problem_admin")
                || SecurityUtils.getSubject().hasRole("admin");

        if (!StringUtils.isEmpty(pid)) {
            discussionQueryWrapper.eq("pid", pid);
        }

        if (!(admin && isAdmin)) {
            discussionQueryWrapper.isNull("gid");
        }

        discussionQueryWrapper
                .eq(!(admin && isAdmin), "status", 0)
                .orderByDesc("top_priority")
                .orderByDesc("gmt_create")
                .orderByDesc("like_num")
                .orderByDesc("view_num");

        if (onlyMine && userRolesVo != null) {
            discussionQueryWrapper.eq("uid", userRolesVo.getUid());
        }
        IPage<Discussion> discussionIPage = discussionEntityService.page(iPage, discussionQueryWrapper);
        List<Discussion> records = discussionIPage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            for (Discussion discussion : records) {
                if (userRolesVo == null) {
                    discussion.setContent(null);
                } else if (!userRolesVo.getUid().equals(discussion.getUid())) {
                    discussion.setContent(null);
                }
            }
        }
        return discussionIPage;
    }

    public DiscussionVO getDiscussion(Integer did) throws StatusNotFoundException, StatusForbiddenException, AccessException {

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        String uid = null;

        if (userRolesVo != null) {
            uid = userRolesVo.getUid();
        }

        DiscussionVO discussionVo = discussionEntityService.getDiscussion(did, uid);

        if (discussionVo == null) {
            throw new StatusNotFoundException("?????????????????????????????????");
        }

        if (discussionVo.getGid() != null && uid == null) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (discussionVo.getStatus() == 1) {
            throw new StatusForbiddenException("????????????????????????????????????");
        }

        if (discussionVo.getGid() != null) {
            accessValidator.validateAccess(HOJAccessEnum.GROUP_DISCUSSION);
            if (!isRoot && !discussionVo.getUid().equals(uid)
                    && !groupValidator.isGroupMember(uid, discussionVo.getGid())) {
                throw new StatusForbiddenException("?????????????????????????????????");
            }
        } else {
            accessValidator.validateAccess(HOJAccessEnum.PUBLIC_DISCUSSION);
        }

        // ?????????+1
        UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
        discussionUpdateWrapper.setSql("view_num=view_num+1").eq("id", discussionVo.getId());
        discussionEntityService.update(discussionUpdateWrapper);
        discussionVo.setViewNum(discussionVo.getViewNum() + 1);

        return discussionVo;
    }

    public void addDiscussion(Discussion discussion) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        commonValidator.validateContent(discussion.getTitle(), "????????????", 255);
        commonValidator.validateContent(discussion.getDescription(), "????????????", 255);
        commonValidator.validateContent(discussion.getContent(), "??????", 65535);
        commonValidator.validateNotEmpty(discussion.getCategoryId(), "????????????");

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        String problemId = discussion.getPid();
        if (problemId != null) {
            QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.eq("problem_id", problemId);
            int problemCount = problemEntityService.count(problemQueryWrapper);
            if (problemCount == 0) {
                throw new StatusNotFoundException("???????????????????????????????????????????????????!");
            }
        }

        if (discussion.getGid() != null) {
            if (!isRoot
                    && !discussion.getUid().equals(userRolesVo.getUid())
                    && !groupValidator.isGroupMember(userRolesVo.getUid(), discussion.getGid())) {
                throw new StatusForbiddenException("?????????????????????????????????");
            }
        }

        // ??????????????? ??????????????????AC20????????????????????????????????????????????????????????????5???
        if (!isRoot && !isProblemAdmin && !isAdmin) {
            QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
            int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);
            SwitchConfig switchConfig = nacosSwitchConfig.getSwitchConfig();
            if (userAcProblemCount < switchConfig.getDefaultCreateDiscussionACInitValue()) {
                throw new StatusForbiddenException("???????????????????????????????????????????????????????????????" + switchConfig.getDefaultCreateDiscussionACInitValue() + "?????????!");
            }

            String lockKey = Constants.Account.DISCUSSION_ADD_NUM_LOCK.getCode() + userRolesVo.getUid();
            Integer num = (Integer) redisUtils.get(lockKey);
            if (num == null) {
                redisUtils.set(lockKey, 1, 3600 * 24);
            } else if (num >= switchConfig.getDefaultCreateDiscussionDailyLimit()) {
                throw new StatusForbiddenException("??????????????????????????????????????????" + switchConfig.getDefaultCreateDiscussionDailyLimit() + "?????????????????????");
            } else {
                redisUtils.incr(lockKey, 1);
            }
        }

        discussion.setAuthor(userRolesVo.getUsername())
                .setAvatar(userRolesVo.getAvatar())
                .setUid(userRolesVo.getUid());

        if (SecurityUtils.getSubject().hasRole("root")) {
            discussion.setRole("root");
        } else if (SecurityUtils.getSubject().hasRole("admin")
                || SecurityUtils.getSubject().hasRole("problem_admin")) {
            discussion.setRole("admin");
        } else {
            // ??????????????????????????????????????????????????????
            discussion.setTopPriority(false);
        }

        boolean isOk = discussionEntityService.saveOrUpdate(discussion);
        if (!isOk) {
            throw new StatusFailException("?????????????????????????????????");
        }
    }


    public void updateDiscussion(Discussion discussion) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        commonValidator.validateNotEmpty(discussion.getId(), "??????ID");
        commonValidator.validateContent(discussion.getTitle(), "????????????", 255);
        commonValidator.validateContent(discussion.getDescription(), "????????????", 255);
        commonValidator.validateContent(discussion.getContent(), "??????", 65535);
        commonValidator.validateNotEmpty(discussion.getCategoryId(), "????????????");

        QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
        discussionQueryWrapper
                .select("id", "uid", "gid")
                .eq("id", discussion.getId());

        Discussion oriDiscussion = discussionEntityService.getOne(discussionQueryWrapper);
        if (oriDiscussion == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        if (!isRoot
                && !oriDiscussion.getUid().equals(userRolesVo.getUid())
                && !(oriDiscussion.getGid() != null
                && groupValidator.isGroupAdmin(userRolesVo.getUid(), oriDiscussion.getGid()))) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }
        UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
        discussionUpdateWrapper.set("title", discussion.getTitle())
                .set("content", discussion.getContent())
                .set("description", discussion.getDescription())
                .set("category_id", discussion.getCategoryId())
                .set(isRoot || isProblemAdmin || isAdmin,
                        "top_priority", discussion.getTopPriority())
                .eq("id", discussion.getId());
        boolean isOk = discussionEntityService.update(discussionUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }

    public void removeDiscussion(Integer did) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
        discussionQueryWrapper
                .select("id", "uid", "gid")
                .eq("id", did);

        Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);
        if (discussion == null) {
            throw new StatusNotFoundException("???????????????????????????????????????");
        }

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        if (!isRoot
                && !discussion.getUid().equals(userRolesVo.getUid())
                && !(discussion.getGid() != null
                && groupValidator.isGroupAdmin(userRolesVo.getUid(), discussion.getGid()))) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<Discussion>().eq("id", did);
        // ????????????????????????,??????????????????????????????uid??????
        if (!SecurityUtils.getSubject().hasRole("root")
                && !SecurityUtils.getSubject().hasRole("admin")
                && !SecurityUtils.getSubject().hasRole("problem_admin")) {
            discussionUpdateWrapper.eq("uid", userRolesVo.getUid());
        }
        boolean isOk = discussionEntityService.remove(discussionUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("????????????????????????????????????????????????");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void addDiscussionLike(Integer did, boolean toLike) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {
        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        Discussion discussion = discussionEntityService.getById(did);

        if (discussion == null) {
            throw new StatusNotFoundException("?????????????????????????????????");
        }

        if (discussion.getGid() != null) {
            boolean isRoot = SecurityUtils.getSubject().hasRole("root");
            if (!isRoot && !discussion.getUid().equals(userRolesVo.getUid())
                    && !groupValidator.isGroupMember(userRolesVo.getUid(), discussion.getGid())) {
                throw new StatusForbiddenException("?????????????????????????????????");
            }
        }

        QueryWrapper<DiscussionLike> discussionLikeQueryWrapper = new QueryWrapper<>();
        discussionLikeQueryWrapper.eq("did", did).eq("uid", userRolesVo.getUid());

        DiscussionLike discussionLike = discussionLikeEntityService.getOne(discussionLikeQueryWrapper, false);

        if (toLike) { // ????????????
            if (discussionLike == null) { // ????????????????????????
                boolean isSave = discussionLikeEntityService.saveOrUpdate(new DiscussionLike().setUid(userRolesVo.getUid()).setDid(did));
                if (!isSave) {
                    throw new StatusFailException("?????????????????????????????????");
                }
            }
            // ??????+1
            UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
            discussionUpdateWrapper.eq("id", discussion.getId())
                    .setSql("like_num=like_num+1");
            discussionEntityService.update(discussionUpdateWrapper);
            // ????????????????????????????????? ?????????????????????
            if (!userRolesVo.getUsername().equals(discussion.getAuthor())) {
                discussionEntityService.updatePostLikeMsg(discussion.getUid(),
                        userRolesVo.getUid(),
                        did,
                        discussion.getGid());
            }
        } else { // ????????????
            if (discussionLike != null) { // ?????????????????????
                boolean isDelete = discussionLikeEntityService.removeById(discussionLike.getId());
                if (!isDelete) {
                    throw new StatusFailException("???????????????????????????????????????");
                }
            }
            // ??????-1
            UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
            discussionUpdateWrapper.setSql("like_num=like_num-1").eq("id", did);
            discussionEntityService.update(discussionUpdateWrapper);
        }

    }

    public List<Category> getDiscussionCategory() {
        return categoryEntityService.list();
    }

    public List<Category> upsertDiscussionCategory(List<Category> categoryList) throws StatusFailException {
        List<Category> categories = categoryList.stream().filter(category -> category.getName() != null
                        && !category.getName().trim().isEmpty())
                .collect(Collectors.toList());
        boolean isOk = categoryEntityService.saveOrUpdateBatch(categories);
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
        return categoryEntityService.list();
    }

    public void addDiscussionReport(DiscussionReport discussionReport) throws StatusFailException {
        boolean isOk = discussionReportEntityService.saveOrUpdate(discussionReport);
        if (!isOk) {
            throw new StatusFailException("??????????????????????????????");
        }
    }
}