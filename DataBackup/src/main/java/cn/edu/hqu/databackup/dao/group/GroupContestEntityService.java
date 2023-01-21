package cn.edu.hqu.databackup.dao.group;

import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.databackup.pojo.vo.ContestVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author: egret
 */
public interface GroupContestEntityService extends IService<Contest> {

    IPage<ContestVO> getContestList(int limit, int currentPage, Long gid);

    IPage<Contest> getAdminContestList(int limit, int currentPage, Long gid);

}
