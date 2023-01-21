package cn.edu.hqu.databackup.dao.group.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.dao.group.GroupTrainingEntityService;
import cn.edu.hqu.databackup.dao.training.TrainingProblemEntityService;
import cn.edu.hqu.databackup.mapper.GroupTrainingMapper;
import cn.edu.hqu.api.pojo.entity.training.Training;
import cn.edu.hqu.api.pojo.entity.training.TrainingProblem;
import cn.edu.hqu.databackup.pojo.vo.TrainingVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Service
public class GroupTrainingEntityServiceImpl extends ServiceImpl<GroupTrainingMapper, Training> implements GroupTrainingEntityService {

    @Autowired
    private GroupTrainingMapper groupTrainingMapper;

    @Resource
    private TrainingProblemEntityService trainingProblemEntityService;

    @Override
    public IPage<TrainingVO> getTrainingList(int limit, int currentPage, Long gid) {
        IPage<TrainingVO> iPage = new Page<>(currentPage, limit);

        List<TrainingVO> trainingList = groupTrainingMapper.getTrainingList(iPage, gid);

        // 当前用户有登录，且训练列表不为空，则查询用户对于每个训练的做题进度
        if (trainingList.size() > 0) {

            AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

            List<Long> tidList = trainingList.stream().map(TrainingVO::getId).collect(Collectors.toList());
            List<TrainingProblem> trainingProblemList = trainingProblemEntityService.getGroupTrainingListAcceptedCountByUid(tidList, gid, userRolesVo.getUid());
            HashMap<Long, Integer> tidMapCount = new HashMap<>(trainingList.size());
            for (TrainingProblem trainingProblem : trainingProblemList) {
                int count = tidMapCount.getOrDefault(trainingProblem.getTid(), 0);
                count++;
                tidMapCount.put(trainingProblem.getTid(), count);
            }

            for (TrainingVO trainingVo : trainingList) {
                Integer count = tidMapCount.getOrDefault(trainingVo.getId(), 0);
                trainingVo.setAcCount(count);
            }
        }

        return iPage.setRecords(trainingList);
    }

    @Override
    public IPage<Training> getAdminTrainingList(int limit, int currentPage, Long gid) {
        IPage<Training> iPage = new Page<>(currentPage, limit);

        List<Training> trainingList = groupTrainingMapper.getAdminTrainingList(iPage, gid);

        return iPage.setRecords(trainingList);
    }

}