package cn.edu.hqu.databackup.manager.group.contest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.dao.common.AnnouncementEntityService;
import cn.edu.hqu.databackup.dao.contest.ContestAnnouncementEntityService;
import cn.edu.hqu.databackup.dao.contest.ContestEntityService;
import cn.edu.hqu.databackup.dao.group.GroupEntityService;
import cn.edu.hqu.databackup.pojo.dto.AnnouncementDTO;
import cn.edu.hqu.api.pojo.entity.common.Announcement;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.contest.ContestAnnouncement;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.databackup.pojo.vo.AnnouncementVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.validator.CommonValidator;
import cn.edu.hqu.databackup.validator.GroupValidator;

/**
 * @Author: egret
 */
@Component
public class GroupContestAnnouncementManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private AnnouncementEntityService announcementEntityService;

    @Autowired
    private ContestAnnouncementEntityService contestAnnouncementEntityService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private CommonValidator commonValidator;

    public IPage<AnnouncementVO> getContestAnnouncementList(Integer limit, Integer currentPage, Long cid) throws StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = contest.getGid();

        if (gid == null){
            throw new StatusForbiddenException("?????????????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUid().equals(contest.getUid()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        return announcementEntityService.getContestAnnouncement(cid, false, limit, currentPage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addContestAnnouncement(AnnouncementDTO announcementDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {

        commonValidator.validateContent(announcementDto.getAnnouncement().getTitle(), "????????????", 255);
        commonValidator.validateContentLength(announcementDto.getAnnouncement().getContent(), "??????", 65535);
        commonValidator.validateNotEmpty(announcementDto.getCid(), "??????ID");

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long cid = announcementDto.getCid();

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = contest.getGid();

        if (gid == null){
            throw new StatusForbiddenException("?????????????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUid().equals(contest.getUid()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        announcementDto.getAnnouncement().setGid(gid);

        boolean isOk = announcementEntityService.save(announcementDto.getAnnouncement());
        if (isOk) {
            contestAnnouncementEntityService.saveOrUpdate(new ContestAnnouncement()
                    .setAid(announcementDto.getAnnouncement().getId())
                    .setCid(announcementDto.getCid()));
        } else {
            throw new StatusFailException("???????????????");
        }
    }

    public void updateContestAnnouncement(AnnouncementDTO announcementDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {

        commonValidator.validateContent(announcementDto.getAnnouncement().getTitle(), "????????????", 255);
        commonValidator.validateContentLength(announcementDto.getAnnouncement().getContent(), "??????", 65535);
        commonValidator.validateNotEmpty(announcementDto.getCid(), "??????ID");

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long cid = announcementDto.getCid();

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = contest.getGid();
        if (gid == null){
            throw new StatusForbiddenException("?????????????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUid().equals(contest.getUid()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = announcementEntityService.updateById(announcementDto.getAnnouncement());
        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }

    public void deleteContestAnnouncement(Long aid, Long cid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Contest contest = contestEntityService.getById(cid);

        if (contest == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = contest.getGid();

        if (gid == null){
            throw new StatusForbiddenException("?????????????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        Announcement announcement = announcementEntityService.getById(aid);

        if (announcement == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!userRolesVo.getUid().equals(contest.getUid()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = announcementEntityService.removeById(aid);
        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }
}
