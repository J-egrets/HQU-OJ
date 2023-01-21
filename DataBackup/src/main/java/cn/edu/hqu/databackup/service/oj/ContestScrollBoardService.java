package cn.edu.hqu.databackup.service.oj;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.vo.ContestScrollBoardInfoVO;
import cn.edu.hqu.databackup.pojo.vo.ContestScrollBoardSubmissionVO;

import java.util.List;

/**
 * @Author: egret
 */
public interface ContestScrollBoardService {

    public CommonResult<ContestScrollBoardInfoVO> getContestScrollBoardInfo(Long cid);

    public CommonResult<List<ContestScrollBoardSubmissionVO>> getContestScrollBoardSubmission(Long cid, Boolean removeStar);
}
