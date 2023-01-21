package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import cn.edu.hqu.api.pojo.entity.training.Training;
import cn.edu.hqu.api.pojo.entity.training.TrainingCategory;

/**
 * @Author: egret
 * @Description: 后台管理训练的传输类
 */
@Data
@Accessors(chain = true)
public class TrainingDTO {

    private Training training;

    private TrainingCategory trainingCategory;
}