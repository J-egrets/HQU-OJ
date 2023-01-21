package cn.edu.hqu.databackup.service.admin.discussion.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.admin.discussion.AdminDiscussionManager;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.api.pojo.entity.discussion.DiscussionReport;
import cn.edu.hqu.databackup.pojo.vo.DiscussionReportVO;
import cn.edu.hqu.databackup.service.admin.discussion.AdminDiscussionService;

import java.util.List;

/**
 * @Author: egret
 */
@Service
public class AdminDiscussionServiceImpl implements AdminDiscussionService {

    @Autowired
    private AdminDiscussionManager adminDiscussionManager;

    @Override
    public CommonResult<Void> updateDiscussion(Discussion discussion) {
        try {
            adminDiscussionManager.updateDiscussion(discussion);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> removeDiscussion(List<Integer> didList) {
        try {
            adminDiscussionManager.removeDiscussion(didList);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<IPage<DiscussionReportVO>> getDiscussionReport(Integer limit, Integer currentPage) {
        IPage<DiscussionReportVO> discussionReportIPage = adminDiscussionManager.getDiscussionReport(limit, currentPage);
        return CommonResult.successResponse(discussionReportIPage);
    }


    @Override
    public CommonResult<Void> updateDiscussionReport(DiscussionReport discussionReport) {
        try {
            adminDiscussionManager.updateDiscussionReport(discussionReport);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}