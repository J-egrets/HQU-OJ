package cn.edu.hqu.databackup.dao.discussion.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.DiscussionLikeMapper;
import cn.edu.hqu.api.pojo.entity.discussion.DiscussionLike;
import cn.edu.hqu.databackup.dao.discussion.DiscussionLikeEntityService;

/**
 * @Author: egret
 */
@Service
public class DiscussionLikeEntityServiceImpl extends ServiceImpl<DiscussionLikeMapper, DiscussionLike> implements DiscussionLikeEntityService {
}