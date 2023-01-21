package cn.edu.hqu.databackup.dao.contest.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.ContestAnnouncementMapper;
import cn.edu.hqu.api.pojo.entity.contest.ContestAnnouncement;
import cn.edu.hqu.databackup.dao.contest.ContestAnnouncementEntityService;

/**
 * @Author: egret
 */
@Service
public class ContestAnnouncementEntityServiceImpl extends ServiceImpl<ContestAnnouncementMapper, ContestAnnouncement> implements ContestAnnouncementEntityService {
}