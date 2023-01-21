package cn.edu.hqu.databackup.service.group.discussion;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.discussion.Discussion;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Author: egret
 */
public interface GroupDiscussionService {

    public CommonResult<IPage<Discussion>> getDiscussionList(Integer limit, Integer currentPage, Long gid, String pid);

    public CommonResult<IPage<Discussion>> getAdminDiscussionList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<Void> addDiscussion(Discussion discussion);

    public CommonResult<Void> updateDiscussion(Discussion discussion);

    public CommonResult<Void> deleteDiscussion(Long did);

}
