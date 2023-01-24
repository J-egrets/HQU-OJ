package cn.edu.hqu.databackup.controller.admin;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.databackup.service.admin.rejudge.RejudgeService;


import javax.annotation.Resource;

/**
 * @author egret
 * @Description: 超管重判提交
 */

@RestController
@RequestMapping("/api/admin/judge")
public class AdminJudgeController {

    @Resource
    private RejudgeService rejudgeService;

    @GetMapping("/rejudge")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Judge> rejudge(@RequestParam("submitId") Long submitId) {
        return rejudgeService.rejudge(submitId);
    }

    @GetMapping("/rejudge-contest-problem")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Void> rejudgeContestProblem(@RequestParam("cid") Long cid, @RequestParam("pid") Long pid) {
        return rejudgeService.rejudgeContestProblem(cid, pid);
    }

    @GetMapping("/manual-judge")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Judge> manualJudge(@RequestParam("submitId") Long submitId,
                                           @RequestParam("status") Integer status,
                                           @RequestParam(value = "score", required = false) Integer score) {
        return rejudgeService.manualJudge(submitId, status, score);
    }

    @GetMapping("/cancel-judge")
    @RequiresAuthentication
    @RequiresRoles("root")  // 只有超级管理员能操作
    @RequiresPermissions("rejudge")
    public CommonResult<Judge> cancelJudge(@RequestParam("submitId") Long submitId) {
        return rejudgeService.cancelJudge(submitId);
    }
}
