package cn.edu.hqu.databackup.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.api.pojo.entity.user.RoleAuth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.edu.hqu.databackup.pojo.vo.RoleAuthsVO;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @Author: egret
 */
@Mapper
@Repository
public interface RoleAuthMapper extends BaseMapper<RoleAuth> {
    RoleAuthsVO getRoleAuths(@Param("rid") long rid);
}
