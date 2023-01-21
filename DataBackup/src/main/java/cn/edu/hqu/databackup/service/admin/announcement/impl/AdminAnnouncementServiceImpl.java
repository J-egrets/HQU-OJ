package cn.edu.hqu.databackup.service.admin.announcement.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.admin.announcement.AdminAnnouncementManager;
import cn.edu.hqu.api.pojo.entity.common.Announcement;
import cn.edu.hqu.databackup.pojo.vo.AnnouncementVO;
import cn.edu.hqu.databackup.service.admin.announcement.AdminAnnouncementService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class AdminAnnouncementServiceImpl implements AdminAnnouncementService {

    @Resource
    private AdminAnnouncementManager adminAnnouncementManager;

    @Override
    public CommonResult<IPage<AnnouncementVO>> getAnnouncementList(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(adminAnnouncementManager.getAnnouncementList(limit, currentPage));
    }

    @Override
    public CommonResult<Void> deleteAnnouncement(Long aid) {
        try {
            adminAnnouncementManager.deleteAnnouncement(aid);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> addAnnouncement(Announcement announcement) {
        try {
            adminAnnouncementManager.addAnnouncement(announcement);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateAnnouncement(Announcement announcement) {
        try {
            adminAnnouncementManager.updateAnnouncement(announcement);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}