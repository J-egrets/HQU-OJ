package cn.edu.hqu.databackup.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.CheckACDTO;
import cn.edu.hqu.api.pojo.entity.contest.ContestPrint;
import cn.edu.hqu.api.pojo.entity.contest.ContestRecord;

/**
 * @author egret
 */
public interface ContestAdminService {

    public CommonResult<IPage<ContestRecord>> getContestACInfo(Long cid, Integer currentPage, Integer limit);

    public CommonResult<Void> checkContestACInfo(CheckACDTO checkACDto);

    public CommonResult<IPage<ContestPrint>> getContestPrint(Long cid, Integer currentPage, Integer limit);

    public CommonResult<Void> checkContestPrintStatus(Long id, Long cid);
}
