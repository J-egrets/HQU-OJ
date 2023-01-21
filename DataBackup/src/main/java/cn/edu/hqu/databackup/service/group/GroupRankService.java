package cn.edu.hqu.databackup.service.group;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.vo.OIRankVO;

/**
 * @author egret
 */
public interface GroupRankService {

    public CommonResult<IPage<OIRankVO>> getGroupRankList(Integer limit,
                                                          Integer currentPage,
                                                          String searchUser,
                                                          Integer type,
                                                          Long gid);
}
