package cn.edu.hqu.databackup.dao.group;

import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author: egret
 */
public interface GroupProblemEntityService extends IService<Problem> {

    IPage<ProblemVO> getProblemList(int limit, int currentPage, Long gid);

    IPage<Problem> getAdminProblemList(int limit, int currentPage, Long gid);

}
