package cn.edu.hqu.databackup.dao.user.impl;

import cn.edu.hqu.api.pojo.entity.user.Role;
import cn.edu.hqu.databackup.mapper.RoleMapper;
import cn.edu.hqu.databackup.dao.user.RoleEntityService;
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
public class RoleEntityServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleEntityService {

}
