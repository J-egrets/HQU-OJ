package cn.edu.hqu.databackup.controller.oj;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.edu.hqu.databackup.annotation.AnonApi;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.vo.ContestScrollBoardInfoVO;
import cn.edu.hqu.databackup.pojo.vo.ContestScrollBoardSubmissionVO;
import cn.edu.hqu.databackup.service.oj.ContestScrollBoardService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: egret
 */
@RestController
@RequestMapping("/api")
@AnonApi
public class ContestScrollBoardController {

    @Resource
    private ContestScrollBoardService contestScrollBoardService;

    @GetMapping("/get-contest-scroll-board-info")
    public CommonResult<ContestScrollBoardInfoVO> getContestScrollBoardInfo(@RequestParam(value = "cid") Long cid) {
        return contestScrollBoardService.getContestScrollBoardInfo(cid);
    }


    @GetMapping("/get-contest-scroll-board-submission")
    public CommonResult<List<ContestScrollBoardSubmissionVO>> getContestScrollBoardSubmission(
            @RequestParam(value = "cid", required = true) Long cid,
            @RequestParam(value = "removeStar", defaultValue = "false") Boolean removeStar) {
        return contestScrollBoardService.getContestScrollBoardSubmission(cid, removeStar);
    }


}
