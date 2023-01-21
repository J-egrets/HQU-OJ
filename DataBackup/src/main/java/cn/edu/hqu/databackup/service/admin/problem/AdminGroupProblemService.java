package cn.edu.hqu.databackup.service.admin.problem;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ChangeGroupProblemProgressDTO;
import cn.edu.hqu.api.pojo.entity.problem.Problem;

/**
 * @Author: egret
 */
public interface AdminGroupProblemService {

    public CommonResult<IPage<Problem>> getProblemList(Integer currentPage, Integer limit, String keyword, Long gid);

    public CommonResult<Void> changeProgress(ChangeGroupProblemProgressDTO changeGroupProblemProgressDto);
}
