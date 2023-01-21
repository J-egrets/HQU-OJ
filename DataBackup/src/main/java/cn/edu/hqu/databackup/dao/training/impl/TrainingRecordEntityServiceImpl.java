package cn.edu.hqu.databackup.dao.training.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.TrainingRecordMapper;
import cn.edu.hqu.api.pojo.entity.training.TrainingRecord;
import cn.edu.hqu.databackup.pojo.vo.TrainingRecordVO;
import cn.edu.hqu.databackup.dao.training.TrainingRecordEntityService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: egret
 */
@Service
public class TrainingRecordEntityServiceImpl extends ServiceImpl<TrainingRecordMapper, TrainingRecord> implements TrainingRecordEntityService {

    @Resource
    private TrainingRecordMapper trainingRecordMapper;

    @Override
    public List<TrainingRecordVO> getTrainingRecord(Long tid){
        return trainingRecordMapper.getTrainingRecord(tid);
    }

}