package cn.edu.hqu.databackup.controller.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;
import cn.edu.hqu.databackup.annotation.AnonApi;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.ContestRankDTO;
import cn.edu.hqu.databackup.pojo.vo.ContestOutsideInfoVO;
import cn.edu.hqu.databackup.service.oj.ContestScoreboardService;

import javax.annotation.Resource;

/**
 * @Author: egret
 * @Description: 处理比赛外榜的相关请求
 */

@RestController
@RequestMapping("/api")
@AnonApi
public class ContestScoreboardController {

    @Resource
    private ContestScoreboardService contestScoreboardService;

    /**
     * @param cid 比赛id
     * @MethodName getContestOutsideInfo
     * @Description 提供比赛外榜所需的比赛信息和题目信息
     * @Return
     */
    @GetMapping("/get-contest-outsize-info")
    public CommonResult<ContestOutsideInfoVO> getContestOutsideInfo(@RequestParam(value = "cid", required = true) Long cid) {
        return contestScoreboardService.getContestOutsideInfo(cid);
    }

    /**
     * @MethodName getContestScoreBoard
     * @Description 提供比赛外榜排名数据
     * @Return
     */
    @PostMapping("/get-contest-outside-scoreboard")
    public CommonResult<IPage> getContestOutsideScoreboard(@RequestBody ContestRankDTO contestRankDto) {
        return contestScoreboardService.getContestOutsideScoreboard(contestRankDto);
    }
}