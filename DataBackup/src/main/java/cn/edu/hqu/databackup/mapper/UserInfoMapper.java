package cn.edu.hqu.databackup.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.edu.hqu.databackup.pojo.dto.RegisterDTO;
import cn.edu.hqu.api.pojo.entity.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author: egret
 */
@Mapper
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    int addUser(RegisterDTO registerDto);

    List<String> getSuperAdminUidList();

    List<String> getProblemAdminUidList();
}
