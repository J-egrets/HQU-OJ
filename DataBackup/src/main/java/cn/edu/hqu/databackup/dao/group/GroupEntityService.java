package cn.edu.hqu.databackup.dao.group;

import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.databackup.pojo.vo.GroupVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author: egret
 */
public interface GroupEntityService extends IService<Group> {
    IPage<GroupVO> getGroupList(int limit,
                                int currentPage,
                                String keyword,
                                Integer auth,
                                String uid,
                                Boolean onlyMine,
                                Boolean isRoot);
}
