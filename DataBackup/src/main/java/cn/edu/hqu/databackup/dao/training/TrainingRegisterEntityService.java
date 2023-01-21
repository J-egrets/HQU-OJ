package cn.edu.hqu.databackup.dao.training;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.training.TrainingRegister;
import java.util.List;

/**
 * @author egret
 */
public interface TrainingRegisterEntityService extends IService<TrainingRegister> {


    public List<String> getAlreadyRegisterUidList(Long tid);

}
