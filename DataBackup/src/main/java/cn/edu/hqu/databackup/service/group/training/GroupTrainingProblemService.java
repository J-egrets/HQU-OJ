package cn.edu.hqu.databackup.service.group.training;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.TrainingProblemDTO;
import cn.edu.hqu.api.pojo.entity.training.TrainingProblem;

import java.util.HashMap;

/**
 * @Author: egret
 */
public interface GroupTrainingProblemService {

    public CommonResult<HashMap<String, Object>> getTrainingProblemList(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, Long tid);

    public CommonResult<Void> updateTrainingProblem(TrainingProblem trainingProblem);

    public CommonResult<Void> deleteTrainingProblem(Long pid, Long tid);

    public CommonResult<Void> addProblemFromPublic(TrainingProblemDTO trainingProblemDto);

    public CommonResult<Void> addProblemFromGroup(String problemId, Long tid);

}
