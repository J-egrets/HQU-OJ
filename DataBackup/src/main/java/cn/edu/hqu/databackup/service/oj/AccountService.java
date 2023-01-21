package cn.edu.hqu.databackup.service.oj;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ChangeEmailDTO;
import cn.edu.hqu.databackup.pojo.dto.ChangePasswordDTO;
import cn.edu.hqu.databackup.pojo.dto.CheckUsernameOrEmailDTO;
import cn.edu.hqu.databackup.pojo.vo.*;


/**
 * @author egret
 */
public interface AccountService {

    public CommonResult<CheckUsernameOrEmailVO> checkUsernameOrEmail(CheckUsernameOrEmailDTO checkUsernameOrEmailDto);

    public CommonResult<UserHomeVO> getUserHomeInfo(String uid, String username);

    public CommonResult<UserCalendarHeatmapVO> getUserCalendarHeatmap(String uid, String username);

    public CommonResult<ChangeAccountVO> changePassword(ChangePasswordDTO changePasswordDto);

    public CommonResult<Void> getChangeEmailCode(String email);

    public CommonResult<ChangeAccountVO> changeEmail(ChangeEmailDTO changeEmailDto);

    public CommonResult<UserInfoVO> changeUserInfo(UserInfoVO userInfoVo);

    public CommonResult<UserAuthInfoVO> getUserAuthInfo();
}
