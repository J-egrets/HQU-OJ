package cn.edu.hqu.databackup.service.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.msg.AdminNoticeManager;
import cn.edu.hqu.api.pojo.entity.msg.AdminSysNotice;
import cn.edu.hqu.databackup.pojo.vo.AdminSysNoticeVO;
import cn.edu.hqu.databackup.service.msg.AdminNoticeService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class AdminNoticeServiceImpl implements AdminNoticeService {

    @Resource
    private AdminNoticeManager adminNoticeManager;

    @Override
    public CommonResult<IPage<AdminSysNoticeVO>> getSysNotice(Integer limit, Integer currentPage, String type) {

        return CommonResult.successResponse(adminNoticeManager.getSysNotice(limit, currentPage, type));
    }

    @Override
    public CommonResult<Void> addSysNotice(AdminSysNotice adminSysNotice) {
        try {
            adminNoticeManager.addSysNotice(adminSysNotice);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteSysNotice(Long id) {
        try {
            adminNoticeManager.deleteSysNotice(id);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateSysNotice(AdminSysNotice adminSysNotice) {
        try {
            adminNoticeManager.updateSysNotice(adminSysNotice);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}