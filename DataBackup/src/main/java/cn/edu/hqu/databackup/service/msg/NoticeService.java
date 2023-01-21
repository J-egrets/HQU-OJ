package cn.edu.hqu.databackup.service.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.vo.SysMsgVO;

/**
 * @author egret
 */
public interface NoticeService {

    public CommonResult<IPage<SysMsgVO>> getSysNotice(Integer limit, Integer currentPage);

    public CommonResult<IPage<SysMsgVO>> getMineNotice(Integer limit, Integer currentPage);
}
