package cn.edu.hqu.databackup.controller.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.edu.hqu.databackup.annotation.AnonApi;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.service.oj.RankService;

/**
 * @Author: egret
 * @Description: 处理排行榜数据
 */
@RestController
@RequestMapping("/api")
@AnonApi
public class RankController {

    @Autowired
    private RankService rankService;

    /**
     * @MethodName get-rank-list
     * @Params * @param null
     * @Description 获取排行榜数据
     * @Return CommonResult
     * @Since 2020/10/27
     */
    @GetMapping("/get-rank-list")
    public CommonResult<IPage> getRankList(@RequestParam(value = "limit", required = false) Integer limit,
                                           @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                           @RequestParam(value = "searchUser", required = false) String searchUser,
                                           @RequestParam(value = "type", required = true) Integer type) {
        return rankService.getRankList(limit, currentPage, searchUser, type);
    }
}