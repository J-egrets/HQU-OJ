package cn.edu.hqu.databackup.service.admin.problem.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.admin.problem.AdminGroupProblemManager;
import cn.edu.hqu.databackup.pojo.dto.ChangeGroupProblemProgressDTO;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.databackup.service.admin.problem.AdminGroupProblemService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class AdminGroupProblemServiceImpl implements AdminGroupProblemService {

    @Resource
    private AdminGroupProblemManager adminGroupProblemManager;

    @Override
    public CommonResult<IPage<Problem>> getProblemList(Integer currentPage, Integer limit, String keyword, Long gid) {
        return CommonResult.successResponse(adminGroupProblemManager.list(currentPage, limit, keyword, gid));
    }

    @Override
    public CommonResult<Void> changeProgress(ChangeGroupProblemProgressDTO changeGroupProblemProgressDto) {
        try {
            adminGroupProblemManager.changeProgress(changeGroupProblemProgressDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }
}
