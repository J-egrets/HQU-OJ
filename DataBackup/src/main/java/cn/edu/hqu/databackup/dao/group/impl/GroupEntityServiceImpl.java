package cn.edu.hqu.databackup.dao.group.impl;

import cn.edu.hqu.databackup.dao.group.GroupEntityService;
import cn.edu.hqu.databackup.mapper.GroupMapper;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.databackup.pojo.vo.GroupVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: egret
 */
@Service
public class GroupEntityServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupEntityService {

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public IPage<GroupVO> getGroupList(int limit,
                                       int currentPage,
                                       String keyword,
                                       Integer auth,
                                       String uid,
                                       Boolean onlyMine,
                                       Boolean isRoot) {
        IPage<GroupVO> iPage = new Page<>(currentPage, limit);
        List<GroupVO> groupList = groupMapper.getGroupList(iPage, keyword, auth, uid, onlyMine,isRoot);

        return iPage.setRecords(groupList);
    }
}
