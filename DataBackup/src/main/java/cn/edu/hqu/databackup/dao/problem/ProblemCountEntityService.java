package cn.edu.hqu.databackup.dao.problem;

import cn.edu.hqu.api.pojo.entity.problem.ProblemCount;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @Author: egret
 */
public interface ProblemCountEntityService extends IService<ProblemCount> {
    ProblemCount getContestProblemCount(Long pid, Long cpid, Long cid);
}
