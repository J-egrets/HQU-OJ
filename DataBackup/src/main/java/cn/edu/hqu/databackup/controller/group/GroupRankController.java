package cn.edu.hqu.databackup.controller.group;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.vo.OIRankVO;
import cn.edu.hqu.databackup.service.group.GroupRankService;

/**
 * @Author: egret
 */
@RestController
@RequestMapping("/api")
public class GroupRankController {

    @Autowired
    private GroupRankService groupRankService;

    @GetMapping("/get-group-rank-list")
    @RequiresAuthentication
    public CommonResult<IPage<OIRankVO>> getRankList(@RequestParam(value = "limit", required = false) Integer limit,
                                                     @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                     @RequestParam(value = "searchUser", required = false) String searchUser,
                                                     @RequestParam(value = "gid", required = true) Long gid,
                                                     @RequestParam(value = "type", required = true) Integer type) {
        return groupRankService.getGroupRankList(limit, currentPage, searchUser, type, gid);
    }
}
