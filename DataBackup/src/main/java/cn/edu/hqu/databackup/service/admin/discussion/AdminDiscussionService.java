package cn.edu.hqu.databackup.service.admin.discussion;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import cn.edu.hqu.api.pojo.entity.discussion.DiscussionReport;
import cn.edu.hqu.databackup.pojo.vo.DiscussionReportVO;

import java.util.List;

/**
 * @Author: egret
 */
public interface AdminDiscussionService {

    public CommonResult<Void> updateDiscussion(Discussion discussion);

    public CommonResult<Void> removeDiscussion(List<Integer> didList);

    public CommonResult<IPage<DiscussionReportVO>> getDiscussionReport(Integer limit, Integer currentPage);

    public CommonResult<Void> updateDiscussionReport(DiscussionReport discussionReport);
}