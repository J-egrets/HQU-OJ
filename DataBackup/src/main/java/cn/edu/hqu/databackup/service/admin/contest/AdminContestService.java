package cn.edu.hqu.databackup.service.admin.contest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.databackup.pojo.vo.AdminContestVO;

/**
 * @author egret
 */
public interface AdminContestService {

    public CommonResult<IPage<Contest>> getContestList(Integer limit, Integer currentPage, String keyword);

    public CommonResult<AdminContestVO> getContest(Long cid);

    public CommonResult<Void> deleteContest(Long cid);

    public CommonResult<Void> addContest(AdminContestVO adminContestVo);

    public CommonResult<Void> cloneContest(Long cid);

    public CommonResult<Void> updateContest(AdminContestVO adminContestVo);

    public CommonResult<Void> changeContestVisible(Long cid, String uid, Boolean visible);

}
