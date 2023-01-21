package cn.edu.hqu.databackup.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.vo.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: egret
 */
public interface HomeService {

    public CommonResult<List<ContestVO>> getRecentContest();

    public CommonResult<List<HashMap<String, Object>>> getHomeCarousel();

    public CommonResult<List<ACMRankVO>> getRecentSevenACRank();

    @Deprecated
    public CommonResult<List<HashMap<String, Object>>> getRecentOtherContest();

    public CommonResult<IPage<AnnouncementVO>> getCommonAnnouncement(Integer limit, Integer currentPage);

    public CommonResult<Map<Object, Object>> getWebConfig();

    public CommonResult<List<RecentUpdatedProblemVO>> getRecentUpdatedProblemList();

    public CommonResult<SubmissionStatisticsVO> getLastWeekSubmissionStatistics(Boolean forceRefresh);
}