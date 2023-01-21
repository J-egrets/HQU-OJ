package cn.edu.hqu.databackup.service.oj;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.LastAcceptedCodeVO;
import cn.edu.hqu.databackup.pojo.dto.PidListDTO;
import cn.edu.hqu.databackup.pojo.vo.ProblemFullScreenListVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemInfoVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemVO;
import cn.edu.hqu.databackup.pojo.vo.RandomProblemVO;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: egret
 */
public interface ProblemService {

    public CommonResult<Page<ProblemVO>> getProblemList(Integer limit, Integer currentPage,
                                                        String keyword, List<Long> tagId, Integer difficulty, String oj);

    public CommonResult<RandomProblemVO> getRandomProblem();

    public CommonResult<HashMap<Long, Object>> getUserProblemStatus(PidListDTO pidListDto);

    public CommonResult<ProblemInfoVO> getProblemInfo(String problemId, Long gid);

    public CommonResult<LastAcceptedCodeVO> getUserLastAcceptedCode(Long pid, Long cid);

    public CommonResult<List<ProblemFullScreenListVO>> getFullScreenProblemList(Long tid, Long cid);

}