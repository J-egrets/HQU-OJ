package cn.edu.hqu.databackup.dao.discussion.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.CommentLikeMapper;
import cn.edu.hqu.api.pojo.entity.discussion.CommentLike;
import cn.edu.hqu.databackup.dao.discussion.CommentLikeEntityService;

/**
 * @Author: egret
 */
@Service
public class CommentLikeEntityServiceImpl extends ServiceImpl<CommentLikeMapper, CommentLike> implements CommentLikeEntityService {
}