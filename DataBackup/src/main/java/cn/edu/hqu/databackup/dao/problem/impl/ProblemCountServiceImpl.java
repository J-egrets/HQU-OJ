package cn.edu.hqu.databackup.dao.problem.impl;

import org.springframework.beans.factory.annotation.Autowired;
import cn.edu.hqu.api.pojo.entity.problem.ProblemCount;
import cn.edu.hqu.databackup.mapper.ProblemCountMapper;
import cn.edu.hqu.databackup.dao.problem.ProblemCountEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @Author: egret
 */
@Service
public class ProblemCountServiceImpl extends ServiceImpl<ProblemCountMapper, ProblemCount> implements ProblemCountEntityService {

    @Autowired
    private ProblemCountMapper problemCountMapper;

    @Override
    public ProblemCount getContestProblemCount(Long pid, Long cpid, Long cid) {
        return problemCountMapper.getContestProblemCount(pid,cpid, cid);
    }
}
