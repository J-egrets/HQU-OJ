package cn.edu.hqu.databackup.dao.training;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.api.pojo.entity.training.Training;
import cn.edu.hqu.databackup.pojo.vo.TrainingVO;

/**
 * @author egret
 */
public interface TrainingEntityService extends IService<Training> {

    public Page<TrainingVO> getTrainingList(int limit,
                                            int currentPage,
                                            Long categoryId,
                                            String auth,
                                            String keyword,
                                            String currentUid);

}
