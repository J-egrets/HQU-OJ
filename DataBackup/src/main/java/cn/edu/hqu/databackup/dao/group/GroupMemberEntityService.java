package cn.edu.hqu.databackup.dao.group;

import cn.edu.hqu.api.pojo.entity.group.GroupMember;
import cn.edu.hqu.databackup.pojo.vo.GroupMemberVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author: egret
 */
public interface GroupMemberEntityService extends IService<GroupMember> {

    IPage<GroupMemberVO> getMemberList(int limit, int currentPage, String keyword, Integer auth, Long gid);

    IPage<GroupMemberVO> getApplyList(int limit, int currentPage, String keyword, Integer auth, Long gid);

    List<String> getGroupRootUidList(Long gid);

    void addApplyNoticeToGroupRoot(Long gid, String groupName, String newMemberUid);

    void addWelcomeNoticeToGroupNewMember(Long gid, String groupName,String memberUid);

    void addRemoveNoticeToGroupMember(Long gid, String groupName, String operator, String memberUid);

    void addDissolutionNoticeToGroupMember(Long gid, String groupName, List<String> groupMemberUidList, String operator);
}
