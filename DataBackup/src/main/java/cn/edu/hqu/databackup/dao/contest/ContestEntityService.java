package cn.edu.hqu.databackup.dao.contest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.pojo.vo.ContestVO;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @Author: egret
 */
public interface ContestEntityService extends IService<Contest> {

    List<ContestVO> getWithinNext14DaysContests();

    IPage<ContestVO> getContestList(Integer limit, Integer currentPage, Integer type, Integer status, String keyword);

    ContestVO getContestInfoById(long cid);
}
