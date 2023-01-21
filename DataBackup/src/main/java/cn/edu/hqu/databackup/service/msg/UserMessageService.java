package cn.edu.hqu.databackup.service.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.vo.UserMsgVO;
import cn.edu.hqu.databackup.pojo.vo.UserUnreadMsgCountVO;

/**
 * @author egret
 */
public interface UserMessageService {

    public CommonResult<UserUnreadMsgCountVO> getUnreadMsgCount();

    public CommonResult<Void> cleanMsg(String type, Long id);

    public CommonResult<IPage<UserMsgVO>> getCommentMsg(Integer limit, Integer currentPage);

    public CommonResult<IPage<UserMsgVO>> getReplyMsg(Integer limit, Integer currentPage);

    public CommonResult<IPage<UserMsgVO>> getLikeMsg(Integer limit, Integer currentPage);

}
