package cn.edu.hqu.databackup.service.oj.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.oj.ContestAdminManager;
import cn.edu.hqu.databackup.pojo.dto.CheckACDTO;
import cn.edu.hqu.api.pojo.entity.contest.ContestPrint;
import cn.edu.hqu.api.pojo.entity.contest.ContestRecord;
import cn.edu.hqu.databackup.service.oj.ContestAdminService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */

@Service
public class ContestAdminServiceImpl implements ContestAdminService {

    @Resource
    private ContestAdminManager contestAdminManager;

    @Override
    public CommonResult<IPage<ContestRecord>> getContestACInfo(Long cid, Integer currentPage, Integer limit) {
        try {
            return CommonResult.successResponse(contestAdminManager.getContestACInfo(cid, currentPage, limit));
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> checkContestACInfo(CheckACDTO checkACDto) {
        try {
            contestAdminManager.checkContestACInfo(checkACDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<IPage<ContestPrint>> getContestPrint(Long cid, Integer currentPage, Integer limit) {
        try {
            return CommonResult.successResponse(contestAdminManager.getContestPrint(cid, currentPage, limit));
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> checkContestPrintStatus(Long id, Long cid) {
        try {
            contestAdminManager.checkContestPrintStatus(id, cid);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }
}