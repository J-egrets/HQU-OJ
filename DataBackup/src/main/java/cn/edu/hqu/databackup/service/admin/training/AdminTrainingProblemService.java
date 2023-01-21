package cn.edu.hqu.databackup.service.admin.training;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.TrainingProblemDTO;
import cn.edu.hqu.api.pojo.entity.training.TrainingProblem;

import java.util.HashMap;

/**
 * @author egret
 */
public interface AdminTrainingProblemService {

    public CommonResult<HashMap<String, Object>> getProblemList(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, Long tid);

    public CommonResult<Void> updateProblem(TrainingProblem trainingProblem);

    public CommonResult<Void> deleteProblem(Long pid,Long tid);

    public CommonResult<Void> addProblemFromPublic(TrainingProblemDTO trainingProblemDto);

    public CommonResult<Void> importTrainingRemoteOJProblem(String name, String problemId, Long tid);
}
