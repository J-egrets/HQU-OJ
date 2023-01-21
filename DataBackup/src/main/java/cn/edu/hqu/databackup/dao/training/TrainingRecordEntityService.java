package cn.edu.hqu.databackup.dao.training;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.training.TrainingRecord;
import cn.edu.hqu.databackup.pojo.vo.TrainingRecordVO;

import java.util.List;


/**
 * @Author: egret
 */
public interface TrainingRecordEntityService extends IService<TrainingRecord> {

    public List<TrainingRecordVO> getTrainingRecord(Long tid);

}