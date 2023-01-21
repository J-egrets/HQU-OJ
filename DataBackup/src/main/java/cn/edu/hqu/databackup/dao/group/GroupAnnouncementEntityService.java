package cn.edu.hqu.databackup.dao.group;

import cn.edu.hqu.api.pojo.entity.common.Announcement;
import cn.edu.hqu.databackup.pojo.vo.AnnouncementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author: egret
 */
public interface GroupAnnouncementEntityService extends IService<Announcement> {

    IPage<AnnouncementVO> getAnnouncementList(int limit, int currentPage, Long gid);

    IPage<AnnouncementVO> getAdminAnnouncementList(int limit, int currentPage, Long gid);

}
