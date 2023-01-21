package cn.edu.hqu.databackup.service.admin.contest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.AnnouncementDTO;
import cn.edu.hqu.databackup.pojo.vo.AnnouncementVO;

/**
 * @author egret
 */
public interface AdminContestAnnouncementService {

    public CommonResult<IPage<AnnouncementVO>> getAnnouncementList(Integer limit, Integer currentPage, Long cid);

    public CommonResult<Void> deleteAnnouncement(Long aid);

    public CommonResult<Void> addAnnouncement(AnnouncementDTO announcementDto);

    public CommonResult<Void> updateAnnouncement(AnnouncementDTO announcementDto);
}
