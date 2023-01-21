package cn.edu.hqu.databackup.service.group.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.group.GroupRankManager;
import cn.edu.hqu.databackup.pojo.vo.OIRankVO;
import cn.edu.hqu.databackup.service.group.GroupRankService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Service
public class GroupRankServiceImpl implements GroupRankService {

    @Resource
    private GroupRankManager groupRankManager;

    @Override
    public CommonResult<IPage<OIRankVO>> getGroupRankList(Integer limit,
                                                          Integer currentPage,
                                                          String searchUser,
                                                          Integer type,
                                                          Long gid) {
        try {
            return CommonResult.successResponse(groupRankManager.getGroupRankList(limit, currentPage, searchUser, type, gid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}