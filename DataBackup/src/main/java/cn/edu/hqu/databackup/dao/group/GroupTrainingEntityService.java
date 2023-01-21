package cn.edu.hqu.databackup.dao.group;

import cn.edu.hqu.api.pojo.entity.training.Training;
import cn.edu.hqu.databackup.pojo.vo.TrainingVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author: egret
 */
public interface GroupTrainingEntityService extends IService<Training> {

    IPage<TrainingVO> getTrainingList(int limit, int currentPage, Long gid);

    IPage<Training> getAdminTrainingList(int limit, int currentPage, Long gid);

}
