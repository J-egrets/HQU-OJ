package cn.edu.hqu.databackup.service.oj.impl;

import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.oj.ContestScrollBoardManager;
import cn.edu.hqu.databackup.pojo.vo.ContestScrollBoardInfoVO;
import cn.edu.hqu.databackup.pojo.vo.ContestScrollBoardSubmissionVO;
import cn.edu.hqu.databackup.service.oj.ContestScrollBoardService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: egret
 */
@Service
public class ContestScrollBoardServiceImpl implements ContestScrollBoardService {

    @Resource
    private ContestScrollBoardManager contestScrollBoardManager;

    @Override
    public CommonResult<ContestScrollBoardInfoVO> getContestScrollBoardInfo(Long cid) {
        try {
            return CommonResult.successResponse(contestScrollBoardManager.getContestScrollBoardInfo(cid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<List<ContestScrollBoardSubmissionVO>> getContestScrollBoardSubmission(Long cid, Boolean removeStar) {
        try {
            return CommonResult.successResponse(contestScrollBoardManager.getContestScrollBoardSubmission(cid, removeStar));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}
