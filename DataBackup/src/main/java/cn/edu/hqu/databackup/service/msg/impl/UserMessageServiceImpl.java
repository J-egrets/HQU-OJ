package cn.edu.hqu.databackup.service.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.msg.UserMessageManager;
import cn.edu.hqu.databackup.pojo.vo.UserMsgVO;
import cn.edu.hqu.databackup.pojo.vo.UserUnreadMsgCountVO;
import cn.edu.hqu.databackup.service.msg.UserMessageService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class UserMessageServiceImpl implements UserMessageService {

    @Resource
    private UserMessageManager userMessageManager;

    @Override
    public CommonResult<UserUnreadMsgCountVO> getUnreadMsgCount() {
        return CommonResult.successResponse(userMessageManager.getUnreadMsgCount());
    }

    @Override
    public CommonResult<Void> cleanMsg(String type, Long id) {
        try {
            userMessageManager.cleanMsg(type, id);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<IPage<UserMsgVO>> getCommentMsg(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(userMessageManager.getCommentMsg(limit, currentPage));
    }

    @Override
    public CommonResult<IPage<UserMsgVO>> getReplyMsg(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(userMessageManager.getReplyMsg(limit, currentPage));
    }

    @Override
    public CommonResult<IPage<UserMsgVO>> getLikeMsg(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(userMessageManager.getLikeMsg(limit, currentPage));
    }
}