package cn.edu.hqu.databackup.dao.problem.impl;

import cn.edu.hqu.api.pojo.entity.problem.Tag;
import cn.edu.hqu.databackup.mapper.TagMapper;
import cn.edu.hqu.databackup.dao.problem.TagEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @Author: egret
 */
@Service
public class TagEntityServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagEntityService {

}
