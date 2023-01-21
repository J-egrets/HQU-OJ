package cn.edu.hqu.databackup.dao.group.impl;

import cn.edu.hqu.databackup.dao.group.GroupDiscussionEntityService;
import cn.edu.hqu.databackup.mapper.GroupDiscussionMapper;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author: egret
 */
@Service
public class GroupDiscussionEntityServiceImpl extends ServiceImpl<GroupDiscussionMapper, Discussion> implements GroupDiscussionEntityService {
}
