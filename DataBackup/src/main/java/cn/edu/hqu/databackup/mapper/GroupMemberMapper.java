package cn.edu.hqu.databackup.mapper;

import cn.edu.hqu.api.pojo.entity.group.GroupMember;
import cn.edu.hqu.databackup.pojo.vo.GroupMemberVO;
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
public interface GroupMemberMapper extends BaseMapper<GroupMember> {
    List<GroupMemberVO> getMemberList(IPage iPage, @Param("keyword") String keyword, @Param("auth") Integer auth, @Param("gid") Long gid);
    List<GroupMemberVO> getApplyList(IPage iPage, @Param("keyword") String keyword, @Param("auth") Integer auth, @Param("gid") Long gid);
}
