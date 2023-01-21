package cn.edu.hqu.databackup.service.group.training;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.TrainingDTO;
import cn.edu.hqu.api.pojo.entity.training.Training;
import cn.edu.hqu.databackup.pojo.vo.TrainingVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Author: egret
 */
public interface GroupTrainingService {

    public CommonResult<IPage<TrainingVO>> getTrainingList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<IPage<Training>> getAdminTrainingList(Integer limit, Integer currentPage, Long gid);

    public CommonResult<TrainingDTO> getTraining(Long tid);

    public CommonResult<Void> addTraining(TrainingDTO trainingDto);

    public CommonResult<Void> updateTraining(TrainingDTO trainingDto);

    public CommonResult<Void> deleteTraining(Long tid);

    public CommonResult<Void> changeTrainingStatus(Long tid, Boolean status);

}
