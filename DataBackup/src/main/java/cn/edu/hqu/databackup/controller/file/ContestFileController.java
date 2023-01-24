package cn.edu.hqu.databackup.controller.file;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.service.file.ContestFileService;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author: egret
 */
@Controller
@RequestMapping("/api/file")
public class ContestFileController {

    @Autowired
    private ContestFileService contestFileService;

    @GetMapping("/download-contest-rank")
    @RequiresAuthentication
    public void downloadContestRank(@RequestParam("cid") Long cid,
                                    @RequestParam("forceRefresh") Boolean forceRefresh,
                                    @RequestParam(value = "removeStar", defaultValue = "false") Boolean removeStar,
                                    HttpServletResponse response) throws StatusFailException, IOException, StatusForbiddenException {
        contestFileService.downloadContestRank(cid, forceRefresh, removeStar, response);
    }

    @GetMapping("/download-contest-ac-submission")
    @RequiresAuthentication
    public void downloadContestACSubmission(@RequestParam("cid") Long cid,
                                            @RequestParam(value = "excludeAdmin", defaultValue = "false") Boolean excludeAdmin,
                                            @RequestParam(value = "splitType", defaultValue = "user") String splitType,
                                            HttpServletResponse response) throws StatusFailException, StatusForbiddenException {

        contestFileService.downloadContestACSubmission(cid, excludeAdmin, splitType, response);
    }

    @GetMapping("/download-contest-print-text")
    @RequiresAuthentication
    public void downloadContestPrintText(@RequestParam("id") Long id, HttpServletResponse response) throws StatusForbiddenException{
        contestFileService.downloadContestPrintText(id, response);
    }

}