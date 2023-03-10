package cn.edu.hqu.databackup.dao.group.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.scheduling.annotation.Async;
import cn.edu.hqu.databackup.dao.group.GroupMemberEntityService;
import cn.edu.hqu.databackup.dao.user.UserInfoEntityService;
import cn.edu.hqu.databackup.manager.msg.AdminNoticeManager;
import cn.edu.hqu.databackup.mapper.GroupMemberMapper;
import cn.edu.hqu.api.pojo.entity.group.GroupMember;
import cn.edu.hqu.api.pojo.entity.user.UserInfo;
import cn.edu.hqu.databackup.pojo.vo.GroupMemberVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Service
public class GroupMemberEntityServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberEntityService {

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Autowired
    private AdminNoticeManager adminNoticeManager;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Override
    public IPage<GroupMemberVO> getMemberList(int limit, int currentPage, String keyword, Integer auth, Long gid) {
        IPage<GroupMemberVO> iPage = new Page<>(currentPage, limit);
        List<GroupMemberVO> memberList = groupMemberMapper.getMemberList(iPage, keyword, auth, gid);

        return iPage.setRecords(memberList);
    }

    @Override
    public IPage<GroupMemberVO> getApplyList(int limit, int currentPage, String keyword, Integer auth, Long gid) {
        IPage<GroupMemberVO> iPage = new Page<>(currentPage, limit);
        List<GroupMemberVO> applyList = groupMemberMapper.getApplyList(iPage, keyword, auth, gid);

        return iPage.setRecords(applyList);
    }

    @Override
    public List<String> getGroupRootUidList(Long gid) {
        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).eq("auth", 5);
        List<GroupMember> groupMembers = groupMemberMapper.selectList(groupMemberQueryWrapper);
        return groupMembers.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    @Async
    @Override
    public void addApplyNoticeToGroupRoot(Long gid, String groupName, String newMemberUid) {
        String title = "????????????????????????(Group Member Application Notice)";
        UserInfo newMemberUser = userInfoEntityService.getById(newMemberUid);
        if (newMemberUser != null) {
            String content = getNewMemberApplyGroupContent(gid, groupName, newMemberUser);
            List<String> groupRootUidList = getGroupRootUidList(gid);
            adminNoticeManager.addSingleNoticeToBatchUser(null, groupRootUidList, title, content, "Mine");
        }
    }

    private String getNewMemberApplyGroupContent(Long gid, String groupName, UserInfo newMemberUser) {
        return "??????????????????[" + gid + "]???**" + groupName
                + "**????????????????????????????????????????????????[" + newMemberUser.getUsername()
                + "](/user-home?username=" + newMemberUser.getUsername() + ")?????????????????????????????????" +
                "????????? [" + groupName + "](/group/" + gid + "/member) ???????????????" +
                "\n\n" +
                "Hello, as the super administrator of the [" + gid + "]???**" + groupName
                + "**??? group, a user ???[" + newMemberUser.getUsername()
                + "](/user-home?username=" + newMemberUser.getUsername() + ")??? is applying to join the group. " +
                "Please go to [" + groupName + "](/group/" + gid + "/member) check and approve!";
    }


    @Async
    @Override
    public void addWelcomeNoticeToGroupNewMember(Long gid, String groupName, String memberUid) {
        String title = "??????????????????(Welcome to The Group)";
        String content = getWelcomeNewMember(gid, groupName);
        adminNoticeManager.addSingleNoticeToUser(null, memberUid, title, content, "Mine");
    }

    public String getWelcomeNewMember(Long gid, String groupName) {
        return "????????????????????????????????????????????????[" + gid + "]???[" + groupName
                + "](/group/" + gid + ")????????????" +
                "\n\n" +
                "Hello, you have passed the approval. Welcome to join the group [" + gid +
                "]???[" + groupName + "](/group/" + gid + ")???!";
    }


    @Async
    @Override
    public void addRemoveNoticeToGroupMember(Long gid, String groupName, String operator, String memberUid) {
        String title = "????????????????????????(Remove Group Member Notice)";
        String content = getRemoveMemberContent(gid, groupName, operator);
        adminNoticeManager.addSingleNoticeToUser(null, memberUid, title, content, "Mine");
    }

    public String getRemoveMemberContent(Long gid, String groupName, String operator) {
        return "?????????????????????[" + gid + "]???[" + groupName + "](/group/" + gid + ")???" +
                "?????????????????????[" + operator + "](/user-home?username=" + operator + ")?????????????????????" +
                "\n\n" +
                "Hello, You have been removed from the group [" + gid +
                "]???[" + groupName + "](/group/" + gid + ")??? by the group admin ???[" + operator + "](/user-home?username=" + operator + ")???!";
    }


    @Async
    @Override
    public void addDissolutionNoticeToGroupMember(Long gid, String groupName, List<String> groupMemberUidList, String operator) {
        String title = "??????????????????(Group Dissolution Notice)";
        String content = getDissolutionGroupContent(gid, groupName, operator);
        adminNoticeManager.addSingleNoticeToBatchUser(null, groupMemberUidList, title, content, "Mine");

    }

    private String getDissolutionGroupContent(Long gid, String groupName, String operator) {
        return "???????????????????????????**[" + gid + "]???" + groupName
                + "???**???????????????????????????[" + operator + "](/user-home?username=" + operator + ")???????????????????????????" +
                "\n\n" +
                "Hello, your team **[" + gid + "]???" + groupName
                + "???** has been dissolved by the administrator???[" + operator
                + "](/user-home?username=" + operator + ")???, please pay attention!";
    }
}
