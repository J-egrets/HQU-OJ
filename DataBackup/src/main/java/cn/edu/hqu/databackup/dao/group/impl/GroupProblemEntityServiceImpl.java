package cn.edu.hqu.databackup.dao.group.impl;

import cn.edu.hqu.databackup.dao.group.GroupProblemEntityService;
import cn.edu.hqu.databackup.mapper.GroupProblemMapper;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: egret
 */
@Service
public class GroupProblemEntityServiceImpl extends ServiceImpl<GroupProblemMapper, Problem> implements GroupProblemEntityService {

    @Autowired
    private GroupProblemMapper groupProblemMapper;

    @Override
    public IPage<ProblemVO> getProblemList(int limit, int currentPage, Long gid) {
        IPage<ProblemVO> iPage = new Page<>(currentPage, limit);

        List<ProblemVO> problemList = groupProblemMapper.getProblemList(iPage, gid);

        return iPage.setRecords(problemList);
    }

    @Override
    public IPage<Problem> getAdminProblemList(int limit, int currentPage, Long gid) {
        IPage<Problem> iPage = new Page<>(currentPage, limit);

        List<Problem> problemList = groupProblemMapper.getAdminProblemList(iPage, gid);

        return iPage.setRecords(problemList);
    }

}
