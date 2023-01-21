package cn.edu.hqu.databackup.dao.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.api.pojo.entity.common.Announcement;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.databackup.pojo.vo.AnnouncementVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @Author: egret
 */
public interface AnnouncementEntityService extends IService<Announcement> {

    IPage<AnnouncementVO> getAnnouncementList(int limit, int currentPage, Boolean notAdmin);

    IPage<AnnouncementVO> getContestAnnouncement(Long cid, Boolean notAdmin, int limit, int currentPage);
}
