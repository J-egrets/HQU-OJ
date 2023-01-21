package cn.edu.hqu.databackup.dao.training;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.training.TrainingProblem;
import cn.edu.hqu.databackup.pojo.vo.ProblemFullScreenListVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;

import java.util.List;

/**
 * @Author: egret
 */
public interface TrainingProblemEntityService extends IService<TrainingProblem> {
    public List<Long> getTrainingProblemIdList(Long tid);

    public List<ProblemVO> getTrainingProblemList(Long tid);

    public Integer getUserTrainingACProblemCount(String uid, Long gid, List<Long> pidList);

    public List<TrainingProblem> getPrivateTrainingProblemListByPid(Long pid, String uid);

    public List<TrainingProblem> getTrainingListAcceptedCountByUid(List<Long> tidList, String uid);

    public List<TrainingProblem> getGroupTrainingListAcceptedCountByUid(List<Long> tidList, Long gid, String uid);

    public List<ProblemFullScreenListVO> getTrainingFullScreenProblemList(Long tid);
}