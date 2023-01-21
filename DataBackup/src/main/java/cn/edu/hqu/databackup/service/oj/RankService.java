package cn.edu.hqu.databackup.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;

/**
 * @author egret
 */
public interface RankService {

    public CommonResult<IPage> getRankList(Integer limit, Integer currentPage, String searchUser, Integer type);
}
