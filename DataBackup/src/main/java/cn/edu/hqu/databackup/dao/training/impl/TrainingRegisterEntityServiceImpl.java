package cn.edu.hqu.databackup.dao.training.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.mapper.TrainingRegisterMapper;
import cn.edu.hqu.api.pojo.entity.training.TrainingRegister;
import cn.edu.hqu.databackup.dao.training.TrainingRegisterEntityService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Service
public class TrainingRegisterEntityServiceImpl extends ServiceImpl<TrainingRegisterMapper, TrainingRegister> implements TrainingRegisterEntityService {

    @Resource
    private TrainingRegisterMapper trainingRegisterMapper;


    @Override
    public List<String> getAlreadyRegisterUidList(Long tid){
        QueryWrapper<TrainingRegister> trainingRegisterQueryWrapper = new QueryWrapper<>();
        trainingRegisterQueryWrapper.eq("tid", tid);
        return trainingRegisterMapper.selectList(trainingRegisterQueryWrapper).stream().map(TrainingRegister::getUid).collect(Collectors.toList());
    }

}