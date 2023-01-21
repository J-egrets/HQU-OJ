package cn.edu.hqu.databackup.service.group;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.databackup.pojo.vo.AccessVO;
import cn.edu.hqu.databackup.pojo.vo.GroupVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Author: egret
 */
public interface GroupService {

    public CommonResult<IPage<GroupVO>> getGroupList(Integer limit, Integer currentPage, String keyword, Integer auth, Boolean onlyMine);

    public CommonResult<Group> getGroup(Long gid);

    public CommonResult<AccessVO> getGroupAccess(Long gid);

    public CommonResult<Integer> getGroupAuth(Long gid);

    public CommonResult<Void> addGroup(Group group);

    public CommonResult<Void> updateGroup(Group group);

    public CommonResult<Void> deleteGroup(Long gid);
}
