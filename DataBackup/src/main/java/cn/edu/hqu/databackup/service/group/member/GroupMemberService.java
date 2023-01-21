package cn.edu.hqu.databackup.service.group.member;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.group.GroupMember;
import cn.edu.hqu.databackup.pojo.vo.GroupMemberVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Author: egret
 */
public interface GroupMemberService {

    public CommonResult<IPage<GroupMemberVO>> getMemberList(Integer limit, Integer currentPage, String keyword, Integer auth, Long gid);

    public CommonResult<IPage<GroupMemberVO>> getApplyList(Integer limit, Integer currentPage, String keyword, Integer auth, Long gid);

    public CommonResult<Void> addMember(Long gid, String code, String reason);

    public CommonResult<Void> updateMember(GroupMember groupMember);

    public CommonResult<Void> deleteMember(String uid, Long gid);

    public CommonResult<Void> exitGroup(Long gid);
}
