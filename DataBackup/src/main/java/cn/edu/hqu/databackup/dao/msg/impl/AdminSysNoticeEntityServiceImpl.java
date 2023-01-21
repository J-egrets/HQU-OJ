package cn.edu.hqu.databackup.dao.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.AdminSysNoticeMapper;

import cn.edu.hqu.api.pojo.entity.msg.AdminSysNotice;
import cn.edu.hqu.databackup.pojo.vo.AdminSysNoticeVO;
import cn.edu.hqu.databackup.dao.msg.AdminSysNoticeEntityService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class AdminSysNoticeEntityServiceImpl extends ServiceImpl<AdminSysNoticeMapper, AdminSysNotice> implements AdminSysNoticeEntityService {

    @Resource
    private AdminSysNoticeMapper adminSysNoticeMapper;

    @Override
    public IPage<AdminSysNoticeVO> getSysNotice(int limit, int currentPage, String type) {
        Page<AdminSysNoticeVO> page = new Page<>(currentPage, limit);
        return adminSysNoticeMapper.getAdminSysNotice(page, type);
    }
}