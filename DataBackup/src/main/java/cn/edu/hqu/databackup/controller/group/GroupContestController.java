package cn.edu.hqu.databackup.controller.group;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.AnnouncementDTO;
import cn.edu.hqu.databackup.pojo.dto.ContestProblemDTO;
import cn.edu.hqu.databackup.pojo.dto.ProblemDTO;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.contest.ContestProblem;
import cn.edu.hqu.databackup.pojo.vo.AdminContestVO;
import cn.edu.hqu.databackup.pojo.vo.AnnouncementVO;
import cn.edu.hqu.databackup.pojo.vo.ContestVO;
import cn.edu.hqu.databackup.service.group.contest.GroupContestAnnouncementService;
import cn.edu.hqu.databackup.service.group.contest.GroupContestProblemService;
import cn.edu.hqu.databackup.service.group.contest.GroupContestService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: egret
 */
@RestController
@RequiresAuthentication
@RequestMapping("/api/group")
public class GroupContestController {

    @Autowired
    private GroupContestService groupContestService;

    @Autowired
    private GroupContestProblemService groupContestProblemService;

    @Autowired
    private GroupContestAnnouncementService groupContestAnnouncementService;

    @GetMapping("/get-contest-list")
    public CommonResult<IPage<ContestVO>> getContestList(@RequestParam(value = "limit", required = false) Integer limit,
                                                         @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                         @RequestParam(value = "gid", required = true) Long gid) {
        return groupContestService.getContestList(limit, currentPage, gid);
    }

    @GetMapping("/get-admin-contest-list")
    public CommonResult<IPage<Contest>> getAdminContestList(@RequestParam(value = "limit", required = false) Integer limit,
                                                            @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                            @RequestParam(value = "gid", required = true) Long gid) {
        return groupContestService.getAdminContestList(limit, currentPage, gid);
    }

    @GetMapping("/contest")
    public CommonResult<AdminContestVO> getContest(@RequestParam("cid") Long cid) {
        return groupContestService.getContest(cid);
    }

    @PostMapping("/contest")
    public CommonResult<Void> addContest(@RequestBody AdminContestVO adminContestVo) {
        return groupContestService.addContest(adminContestVo);
    }

    @PutMapping("/contest")
    public CommonResult<Void> updateContest(@RequestBody AdminContestVO adminContestVo) {
        return groupContestService.updateContest(adminContestVo);
    }

    @DeleteMapping("/contest")
    public CommonResult<Void> deleteContest(@RequestParam(value = "cid", required = true) Long cid) {
        return groupContestService.deleteContest(cid);
    }

    @PutMapping("/change-contest-visible")
    public CommonResult<Void> changeContestVisible(@RequestParam(value = "cid", required = true) Long cid,
                                                   @RequestParam(value = "visible", required = true) Boolean visible) {
        return groupContestService.changeContestVisible(cid, visible);
    }

    @GetMapping("/get-contest-problem-list")
    public CommonResult<HashMap<String, Object>> getContestProblemList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                                @RequestParam(value = "cid", required = true) Long cid,
                                                                @RequestParam(value = "problemType", required = false) Integer problemType,
                                                                @RequestParam(value = "oj", required = false) String oj) {
        return groupContestProblemService.getContestProblemList(limit, currentPage, keyword, cid, problemType, oj);
    }

    @PostMapping("/contest-problem")
    public CommonResult<Map<Object, Object>> addProblem(@RequestBody ProblemDTO problemDto) {

        return groupContestProblemService.addProblem(problemDto);
    }

    @GetMapping("/contest-problem")
    public CommonResult<ContestProblem> getContestProblem(@RequestParam(value = "pid", required = true) Long pid,
                                                          @RequestParam(value = "cid", required = true) Long cid) {

        return groupContestProblemService.getContestProblem(pid, cid);
    }

    @PutMapping("/contest-problem")
    public CommonResult<Void> updateContestProblem(@RequestBody ContestProblem contestProblem) {

        return groupContestProblemService.updateContestProblem(contestProblem);
    }

    @DeleteMapping("/contest-problem")
    public CommonResult<Void> deleteContestProblem(@RequestParam(value = "pid", required = true) Long pid,
                                                   @RequestParam(value = "cid", required = true) Long cid) {
        return groupContestProblemService.deleteContestProblem(pid, cid);
    }

    @PostMapping("/add-contest-problem-from-public")
    public CommonResult<Void> addProblemFromPublic(@RequestBody ContestProblemDTO contestProblemDto) {
        return groupContestProblemService.addProblemFromPublic(contestProblemDto);
    }

    @PostMapping("/add-contest-problem-from-group")
    public CommonResult<Void> addProblemFromGroup(@RequestParam(value = "problemId", required = true) String problemId,
                                                  @RequestParam(value = "cid", required = true) Long cid,
                                                  @RequestParam(value = "displayId", required = true) String displayId) {
        return groupContestProblemService.addProblemFromGroup(problemId, cid, displayId);
    }

    @GetMapping("/get-contest-announcement-list")
    public CommonResult<IPage<AnnouncementVO>> getContestAnnouncementList(@RequestParam(value = "limit", required = false) Integer limit,
                                                                          @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                                          @RequestParam(value = "cid", required = true) Long cid) {
        return groupContestAnnouncementService.getContestAnnouncementList(limit, currentPage, cid);
    }

    @PostMapping("/contest-announcement")
    public CommonResult<Void> addContestAnnouncement(@RequestBody AnnouncementDTO announcementDto) {
        return groupContestAnnouncementService.addContestAnnouncement(announcementDto);
    }

    @PutMapping("/contest-announcement")
    public CommonResult<Void> updateContestAnnouncement(@RequestBody AnnouncementDTO announcementDto) {
        return groupContestAnnouncementService.updateContestAnnouncement(announcementDto);
    }

    @DeleteMapping("/contest-announcement")
    public CommonResult<Void> deleteContestAnnouncement(@RequestParam(value = "aid", required = true) Long aid,
                                                        @RequestParam(value = "cid", required = true) Long cid) {
        return groupContestAnnouncementService.deleteContestAnnouncement(aid, cid);
    }
}
