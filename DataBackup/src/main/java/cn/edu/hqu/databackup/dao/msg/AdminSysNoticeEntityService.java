package cn.edu.hqu.databackup.dao.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.msg.AdminSysNotice;
import cn.edu.hqu.databackup.pojo.vo.AdminSysNoticeVO;

/**
 * @Author: egret
 */
public interface AdminSysNoticeEntityService extends IService<AdminSysNotice> {

    public IPage<AdminSysNoticeVO> getSysNotice(int limit, int currentPage, String type);

}