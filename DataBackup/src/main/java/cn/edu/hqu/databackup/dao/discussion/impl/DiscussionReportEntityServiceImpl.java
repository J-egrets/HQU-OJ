package cn.edu.hqu.databackup.dao.discussion.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.dao.discussion.DiscussionReportEntityService;
import cn.edu.hqu.databackup.mapper.DiscussionReportMapper;
import cn.edu.hqu.api.pojo.entity.discussion.DiscussionReport;
import cn.edu.hqu.databackup.pojo.vo.DiscussionReportVO;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class DiscussionReportEntityServiceImpl extends ServiceImpl<DiscussionReportMapper, DiscussionReport> implements DiscussionReportEntityService {

    @Resource
    private DiscussionReportMapper discussionReportMapper;

    @Override
    public IPage<DiscussionReportVO> getDiscussionReportList(Integer limit, Integer currentPage) {
        Page<DiscussionReportVO> page = new Page<>(currentPage, limit);
        return discussionReportMapper.getDiscussionReportList(page);
    }
}