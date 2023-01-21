package cn.edu.hqu.databackup.dao.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.MsgRemindMapper;
import cn.edu.hqu.api.pojo.entity.msg.MsgRemind;
import cn.edu.hqu.databackup.pojo.vo.UserMsgVO;
import cn.edu.hqu.databackup.pojo.vo.UserUnreadMsgCountVO;
import cn.edu.hqu.databackup.dao.msg.MsgRemindEntityService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class MsgRemindEntityServiceImpl extends ServiceImpl<MsgRemindMapper, MsgRemind> implements MsgRemindEntityService {

    @Resource
    private MsgRemindMapper msgRemindMapper;
    @Override
    public UserUnreadMsgCountVO getUserUnreadMsgCount(String uid) {
        return msgRemindMapper.getUserUnreadMsgCount(uid);
    }

    @Override
    public IPage<UserMsgVO> getUserMsg(Page<UserMsgVO> page, String uid, String action) {
        return msgRemindMapper.getUserMsg(page, uid, action);
    }

}