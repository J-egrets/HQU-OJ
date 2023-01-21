package cn.edu.hqu.databackup.service.group.contest;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.databackup.pojo.vo.AdminContestVO;
import cn.edu.hqu.databackup.pojo.vo.ContestVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Author: egret
 */
public interface GroupContestService {

    public CommonResult<IPage<ContestVO>> getContestList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<IPage<Contest>> getAdminContestList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<AdminContestVO> getContest(Long cid);

    public CommonResult<Void> addContest(AdminContestVO adminContestVo);

    public CommonResult<Void> updateContest(AdminContestVO adminContestVo);

    public CommonResult<Void> deleteContest(Long cid);

    public CommonResult<Void> changeContestVisible(Long cid, Boolean visible);

}
