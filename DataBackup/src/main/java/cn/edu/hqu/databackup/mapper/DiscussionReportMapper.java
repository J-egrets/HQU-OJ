package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.discussion.DiscussionReport;
import cn.edu.hqu.databackup.pojo.vo.DiscussionReportVO;

/**
 * @author egret
 */
@Mapper
@Repository
public interface DiscussionReportMapper extends BaseMapper<DiscussionReport> {

    IPage<DiscussionReportVO> getDiscussionReportList(Page<DiscussionReportVO> page);
}
