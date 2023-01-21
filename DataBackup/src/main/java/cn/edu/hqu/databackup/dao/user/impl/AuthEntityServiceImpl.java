package cn.edu.hqu.databackup.dao.user.impl;

import cn.edu.hqu.api.pojo.entity.user.Auth;
import cn.edu.hqu.databackup.mapper.AuthMapper;
import cn.edu.hqu.databackup.dao.user.AuthEntityService;
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
public class AuthEntityServiceImpl extends ServiceImpl<AuthMapper, Auth> implements AuthEntityService {

}
