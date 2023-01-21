package cn.edu.hqu.databackup.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ContestPrintDTO;
import cn.edu.hqu.databackup.pojo.dto.ContestRankDTO;
import cn.edu.hqu.databackup.pojo.dto.RegisterContestDTO;
import cn.edu.hqu.databackup.pojo.dto.UserReadContestAnnouncementDTO;
import cn.edu.hqu.api.pojo.entity.common.Announcement;
import cn.edu.hqu.databackup.pojo.vo.*;

import java.util.List;

/**
 * @author egret
 */
public interface ContestService {

    public CommonResult<IPage<ContestVO>> getContestList(Integer limit, Integer currentPage, Integer status, Integer type, String keyword);

    public CommonResult<ContestVO> getContestInfo(Long cid);

    public CommonResult<Void> toRegisterContest(RegisterContestDTO registerContestDto);

    public CommonResult<AccessVO> getContestAccess(Long cid);

    public CommonResult<List<ContestProblemVO>> getContestProblem(Long cid);

    public CommonResult<ProblemInfoVO> getContestProblemDetails(Long cid, String displayId);

    public CommonResult<IPage<JudgeVO>> getContestSubmissionList(Integer limit,
                                                                 Integer currentPage,
                                                                 Boolean onlyMine,
                                                                 String displayId,
                                                                 Integer searchStatus,
                                                                 String searchUsername,
                                                                 Long searchCid,
                                                                 Boolean beforeContestSubmit,
                                                                 Boolean completeProblemID);

    public CommonResult<IPage> getContestRank(ContestRankDTO contestRankDto);

    public CommonResult<IPage<AnnouncementVO>> getContestAnnouncement(Long cid, Integer limit, Integer currentPage);

    public CommonResult<List<Announcement>> getContestUserNotReadAnnouncement(UserReadContestAnnouncementDTO userReadContestAnnouncementDto);

    public CommonResult<Void> submitPrintText(ContestPrintDTO contestPrintDto);

}
