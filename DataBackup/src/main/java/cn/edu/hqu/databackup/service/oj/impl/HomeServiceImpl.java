package cn.edu.hqu.databackup.service.oj.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.oj.HomeManager;
import cn.edu.hqu.databackup.pojo.vo.*;
import cn.edu.hqu.databackup.service.oj.HomeService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: egret
 */
@Service
public class HomeServiceImpl implements HomeService {

    @Resource
    private HomeManager homeManager;

    @Override
    public CommonResult<List<ContestVO>> getRecentContest() {
        return CommonResult.successResponse(homeManager.getRecentContest());
    }

    @Override
    public CommonResult<List<HashMap<String, Object>>> getHomeCarousel() {
        return CommonResult.successResponse(homeManager.getHomeCarousel());
    }

    @Override
    public CommonResult<List<ACMRankVO>> getRecentSevenACRank() {
        return CommonResult.successResponse(homeManager.getRecentSevenACRank());
    }

    @Override
    public CommonResult<List<HashMap<String, Object>>> getRecentOtherContest() {
        return CommonResult.successResponse(homeManager.getRecentOtherContest());
    }

    @Override
    public CommonResult<IPage<AnnouncementVO>> getCommonAnnouncement(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(homeManager.getCommonAnnouncement(limit, currentPage));
    }

    @Override
    public CommonResult<Map<Object, Object>> getWebConfig() {
        return CommonResult.successResponse(homeManager.getWebConfig());
    }

    @Override
    public CommonResult<List<RecentUpdatedProblemVO>> getRecentUpdatedProblemList() {
        return CommonResult.successResponse(homeManager.getRecentUpdatedProblemList());
    }

    @Override
    public CommonResult<SubmissionStatisticsVO> getLastWeekSubmissionStatistics(Boolean forceRefresh) {
        return CommonResult.successResponse(homeManager.getLastWeekSubmissionStatistics(forceRefresh));
    }
}