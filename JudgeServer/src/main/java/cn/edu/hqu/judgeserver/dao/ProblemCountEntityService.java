package cn.edu.hqu.judgeserver.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.problem.ProblemCount;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author egret
 */
public interface ProblemCountEntityService extends IService<ProblemCount> {

    void updateCount(int status, Long pid);

}
