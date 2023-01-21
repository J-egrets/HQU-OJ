package cn.edu.hqu.databackup.dao.discussion;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.discussion.DiscussionReport;
import cn.edu.hqu.databackup.pojo.vo.DiscussionReportVO;

/**
 * @author egret
 */
public interface DiscussionReportEntityService extends IService<DiscussionReport> {

    IPage<DiscussionReportVO> getDiscussionReportList(Integer limit, Integer currentPage);
}
