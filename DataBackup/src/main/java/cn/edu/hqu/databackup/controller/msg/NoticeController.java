package cn.edu.hqu.databackup.controller.msg;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.vo.SysMsgVO;
import cn.edu.hqu.databackup.service.msg.NoticeService;

import javax.annotation.Resource;

/**
 * @Author: egret
 * @Description: 负责用户的 系统消息模块、我的消息模块
 */
@RestController
@RequestMapping("/api/msg")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @RequestMapping(value = "/sys", method = RequestMethod.GET)
    @RequiresAuthentication
    public CommonResult<IPage<SysMsgVO>> getSysNotice(@RequestParam(value = "limit", required = false) Integer limit,
                                                      @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        return noticeService.getSysNotice(limit, currentPage);
    }


    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    @RequiresAuthentication
    public CommonResult<IPage<SysMsgVO>> getMineNotice(@RequestParam(value = "limit", required = false) Integer limit,
                                                       @RequestParam(value = "currentPage", required = false) Integer currentPage) {

        return noticeService.getMineNotice(limit, currentPage);
    }
}