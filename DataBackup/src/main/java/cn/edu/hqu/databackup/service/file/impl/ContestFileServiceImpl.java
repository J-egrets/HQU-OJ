package cn.edu.hqu.databackup.service.file.impl;

import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.manager.file.ContestFileManager;
import cn.edu.hqu.databackup.service.file.ContestFileService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: egret
 */
@Service
public class ContestFileServiceImpl implements ContestFileService {

    @Resource
    private ContestFileManager contestFileManager;

    @Override
    public void downloadContestRank(Long cid, Boolean forceRefresh, Boolean removeStar, HttpServletResponse response) throws StatusFailException, IOException, StatusForbiddenException {
        contestFileManager.downloadContestRank(cid, forceRefresh, removeStar, response);
    }

    @Override
    public void downloadContestACSubmission(Long cid, Boolean excludeAdmin, String splitType, HttpServletResponse response) throws StatusFailException, StatusForbiddenException {
        contestFileManager.downloadContestACSubmission(cid, excludeAdmin, splitType, response);
    }

    @Override
    public void downloadContestPrintText(Long id, HttpServletResponse response) throws StatusForbiddenException {
        contestFileManager.downloadContestPrintText(id, response);
    }
}