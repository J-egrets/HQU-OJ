package cn.edu.hqu.databackup.service.admin.training;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.TrainingDTO;
import cn.edu.hqu.api.pojo.entity.training.Training;

/**
 * @author egret
 */
public interface AdminTrainingService {

    public CommonResult<IPage<Training>> getTrainingList(Integer limit, Integer currentPage, String keyword);

    public CommonResult<TrainingDTO> getTraining(Long tid);

    public CommonResult<Void> deleteTraining(Long tid);

    public CommonResult<Void> addTraining(TrainingDTO trainingDto);

    public CommonResult<Void> updateTraining(TrainingDTO trainingDto);

    public CommonResult<Void> changeTrainingStatus(Long tid, String author, Boolean status);
}
