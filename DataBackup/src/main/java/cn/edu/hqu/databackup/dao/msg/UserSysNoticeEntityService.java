package cn.edu.hqu.databackup.dao.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.msg.UserSysNotice;
import cn.edu.hqu.databackup.pojo.vo.SysMsgVO;

/**
 * @author egret
 */
public interface UserSysNoticeEntityService extends IService<UserSysNotice> {

    IPage<SysMsgVO> getSysNotice(int limit, int currentPage, String uid);

    IPage<SysMsgVO> getMineNotice(int limit, int currentPage, String uid);
}