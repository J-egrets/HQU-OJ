package cn.edu.hqu.databackup.service.oj.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.oj.ContestScoreboardManager;
import cn.edu.hqu.databackup.pojo.dto.ContestRankDTO;
import cn.edu.hqu.databackup.pojo.vo.ContestOutsideInfoVO;
import cn.edu.hqu.databackup.service.oj.ContestScoreboardService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class ContestScoreboardServiceImpl implements ContestScoreboardService {

    @Resource
    private ContestScoreboardManager contestScoreboardManager;

    @Override
    public CommonResult<ContestOutsideInfoVO> getContestOutsideInfo(Long cid) {
        try {
            return CommonResult.successResponse(contestScoreboardManager.getContestOutsideInfo(cid));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<IPage> getContestOutsideScoreboard(ContestRankDTO contestRankDto) {
        try {
            return CommonResult.successResponse(contestScoreboardManager.getContestOutsideScoreboard(contestRankDto));
        }  catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}