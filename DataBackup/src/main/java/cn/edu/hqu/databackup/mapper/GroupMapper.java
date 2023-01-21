package cn.edu.hqu.databackup.mapper;

import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.databackup.pojo.vo.GroupVO;
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
public interface GroupMapper extends BaseMapper<Group> {
    List<GroupVO> getGroupList(IPage iPage,
                               @Param("keyword") String keyword,
                               @Param("auth") Integer auth,
                               @Param("uid") String uid,
                               @Param("onlyMine") Boolean onlyMine,
                               @Param("isRoot") Boolean isRoot);
}
