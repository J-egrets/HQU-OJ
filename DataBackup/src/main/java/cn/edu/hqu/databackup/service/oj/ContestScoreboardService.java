package cn.edu.hqu.databackup.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ContestRankDTO;
import cn.edu.hqu.databackup.pojo.vo.ContestOutsideInfoVO;

/**
 * @Author: egret
 */
public interface ContestScoreboardService {

    public CommonResult<ContestOutsideInfoVO> getContestOutsideInfo(Long cid);

    public CommonResult<IPage> getContestOutsideScoreboard(ContestRankDTO contestRankDto);

}