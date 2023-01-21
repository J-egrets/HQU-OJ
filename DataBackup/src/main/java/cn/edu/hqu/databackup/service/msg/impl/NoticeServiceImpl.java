package cn.edu.hqu.databackup.service.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.msg.NoticeManager;
import cn.edu.hqu.databackup.pojo.vo.SysMsgVO;
import cn.edu.hqu.databackup.service.msg.NoticeService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeManager noticeManager;

    @Override
    public CommonResult<IPage<SysMsgVO>> getSysNotice(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(noticeManager.getSysNotice(limit, currentPage));
    }

    @Override
    public CommonResult<IPage<SysMsgVO>> getMineNotice(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(noticeManager.getMineNotice(limit, currentPage));
    }
}