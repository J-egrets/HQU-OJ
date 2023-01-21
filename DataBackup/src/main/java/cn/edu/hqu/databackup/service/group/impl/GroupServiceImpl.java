package cn.edu.hqu.databackup.service.group.impl;

import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.group.GroupManager;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.databackup.pojo.vo.AccessVO;
import cn.edu.hqu.databackup.pojo.vo.GroupVO;
import cn.edu.hqu.databackup.service.group.GroupService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: egret
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupManager groupManager;

    @Override
    public CommonResult<IPage<GroupVO>> getGroupList(Integer limit, Integer currentPage, String keyword, Integer auth, Boolean onlyMine) {
        return CommonResult.successResponse(groupManager.getGroupList(limit, currentPage, keyword, auth, onlyMine));
    }

    @Override
    public CommonResult<Group> getGroup(Long gid) {
        try {
            return CommonResult.successResponse(groupManager.getGroup(gid));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<AccessVO> getGroupAccess(Long gid) {
        try {
            return CommonResult.successResponse(groupManager.getGroupAccess(gid));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

    @Override
    public CommonResult<Integer> getGroupAuth(Long gid) {
        return CommonResult.successResponse(groupManager.getGroupAuth(gid));
    }

    @Override
    public CommonResult<Void> addGroup(Group group) {
        try {
            groupManager.addGroup(group);
            return CommonResult.successResponse();
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

    @Override
    public CommonResult<Void> updateGroup(Group group) {
        try {
            groupManager.updateGroup(group);
            return CommonResult.successResponse();
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }

    @Override
    public CommonResult<Void> deleteGroup(Long gid) {
        try {
            groupManager.deleteGroup(gid);
            return CommonResult.successResponse();
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FAIL);
        }
    }
}
