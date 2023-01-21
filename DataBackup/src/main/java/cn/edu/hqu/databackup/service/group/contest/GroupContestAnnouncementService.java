package cn.edu.hqu.databackup.service.group.contest;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.AnnouncementDTO;
import cn.edu.hqu.databackup.pojo.vo.AnnouncementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Author: egret
 */
public interface GroupContestAnnouncementService {

    public CommonResult<IPage<AnnouncementVO>> getContestAnnouncementList(Integer limit, Integer currentPage, Long cid);

    public CommonResult<Void> addContestAnnouncement(AnnouncementDTO announcementDto);

    public CommonResult<Void> updateContestAnnouncement(AnnouncementDTO announcementDto);

    public CommonResult<Void> deleteContestAnnouncement(Long aid, Long cid);

}
