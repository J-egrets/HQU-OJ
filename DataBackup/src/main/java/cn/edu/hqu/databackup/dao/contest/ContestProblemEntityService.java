package cn.edu.hqu.databackup.dao.contest;

import cn.edu.hqu.api.pojo.entity.contest.ContestProblem;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.edu.hqu.databackup.pojo.vo.ContestProblemVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemFullScreenListVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @Author: egret
 */
public interface ContestProblemEntityService extends IService<ContestProblem> {
    List<ContestProblemVO> getContestProblemList(Long cid,
                                                 Date startTime,
                                                 Date endTime,
                                                 Date sealTime,
                                                 Boolean isAdmin,
                                                 String contestAuthorUid,
                                                 List<String> groupRootUidList);

    List<ProblemFullScreenListVO> getContestFullScreenProblemList(Long cid);

    void syncContestRecord(Long pid, Long cid, String displayId);
}
