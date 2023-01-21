package cn.edu.hqu.databackup.mapper;

import cn.edu.hqu.api.pojo.entity.common.Announcement;
import cn.edu.hqu.databackup.pojo.vo.AnnouncementVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: egret
 */
@Mapper
@Repository
public interface GroupAnnouncementMapper extends BaseMapper<Announcement> {

    List<AnnouncementVO> getAnnouncementList(IPage iPage, @Param("gid") Long gid);

    List<AnnouncementVO> getAdminAnnouncementList(IPage iPage, @Param("gid") Long gid);

}
