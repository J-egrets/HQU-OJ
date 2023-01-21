package cn.edu.hqu.databackup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.common.Announcement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.edu.hqu.databackup.pojo.vo.AnnouncementVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @Author: egret
 */
@Mapper
@Repository
public interface AnnouncementMapper extends BaseMapper<Announcement> {
    IPage<AnnouncementVO> getAnnouncementList(Page<AnnouncementVO> page, @Param("notAdmin") Boolean notAdmin);
    IPage<AnnouncementVO> getContestAnnouncement(Page<AnnouncementVO> page, @Param("cid")Long cid, @Param("notAdmin") Boolean notAdmin);
}
