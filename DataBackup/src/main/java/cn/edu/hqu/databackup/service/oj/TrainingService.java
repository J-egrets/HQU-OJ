package cn.edu.hqu.databackup.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.RegisterTrainingDTO;
import cn.edu.hqu.databackup.pojo.vo.AccessVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;
import cn.edu.hqu.databackup.pojo.vo.TrainingRankVO;
import cn.edu.hqu.databackup.pojo.vo.TrainingVO;

import java.util.List;

/**
 * @Author: egret
 */
public interface TrainingService {

    public CommonResult<IPage<TrainingVO>> getTrainingList(Integer limit, Integer currentPage,
                                                           String keyword, Long categoryId, String auth);

    public CommonResult<TrainingVO> getTraining(Long tid);

    public CommonResult<List<ProblemVO>> getTrainingProblemList(Long tid);

    public CommonResult<Void> toRegisterTraining(RegisterTrainingDTO registerTrainingDto);

    public CommonResult<AccessVO> getTrainingAccess(Long tid);

    public CommonResult<IPage<TrainingRankVO>> getTrainingRank(Long tid, Integer limit, Integer currentPage,String keyword);
}