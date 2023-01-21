package cn.edu.hqu.databackup.service.admin.contest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.admin.contest.AdminContestProblemManager;
import cn.edu.hqu.databackup.pojo.dto.ContestProblemDTO;
import cn.edu.hqu.databackup.pojo.dto.ProblemDTO;
import cn.edu.hqu.api.pojo.entity.contest.ContestProblem;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.databackup.service.admin.contest.AdminContestProblemService;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: egret
 */

@Service
public class AdminContestProblemServiceImpl implements AdminContestProblemService {

    @Autowired
    private AdminContestProblemManager adminContestProblemManager;

    @Override
    public CommonResult<HashMap<String, Object>> getProblemList(Integer limit, Integer currentPage, String keyword, Long cid, Integer problemType, String oj) {
        HashMap<String, Object> problemList = adminContestProblemManager.getProblemList(limit, currentPage, keyword, cid, problemType, oj);
        return CommonResult.successResponse(problemList);
    }

    @Override
    public CommonResult<Problem> getProblem(Long pid) {
        try {
            Problem problem = adminContestProblemManager.getProblem(pid);
            return CommonResult.successResponse(problem);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> deleteProblem(Long pid, Long cid) {
        adminContestProblemManager.deleteProblem(pid, cid);
        return CommonResult.successResponse();
    }

    @Override
    public CommonResult<Map<Object, Object>> addProblem(ProblemDTO problemDto) {
        try {
            Map<Object, Object> problemMap = adminContestProblemManager.addProblem(problemDto);
            return CommonResult.successResponse(problemMap);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateProblem(ProblemDTO problemDto) {
        try {
            adminContestProblemManager.updateProblem(problemDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<ContestProblem> getContestProblem(Long cid, Long pid) {
        try {
            ContestProblem contestProblem = adminContestProblemManager.getContestProblem(cid, pid);
            return CommonResult.successResponse(contestProblem);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<ContestProblem> setContestProblem(ContestProblem contestProblem) {
        try {
            return CommonResult.successResponse(adminContestProblemManager.setContestProblem(contestProblem));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> addProblemFromPublic(ContestProblemDTO contestProblemDto) {
        try {
            adminContestProblemManager.addProblemFromPublic(contestProblemDto);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> importContestRemoteOJProblem(String name, String problemId, Long cid, String displayId) {
        try {
            adminContestProblemManager.importContestRemoteOJProblem(name, problemId, cid, displayId);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}