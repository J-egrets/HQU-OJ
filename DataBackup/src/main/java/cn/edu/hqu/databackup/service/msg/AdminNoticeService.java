package cn.edu.hqu.databackup.service.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.msg.AdminSysNotice;
import cn.edu.hqu.databackup.pojo.vo.AdminSysNoticeVO;

/**
 * @author egret
 */
public interface AdminNoticeService {

    public CommonResult<IPage<AdminSysNoticeVO>> getSysNotice(Integer limit, Integer currentPage, String type);

    public CommonResult<Void> addSysNotice(AdminSysNotice adminSysNotice);

    public CommonResult<Void> deleteSysNotice(Long id);

    public CommonResult<Void> updateSysNotice(AdminSysNotice adminSysNotice);
}
