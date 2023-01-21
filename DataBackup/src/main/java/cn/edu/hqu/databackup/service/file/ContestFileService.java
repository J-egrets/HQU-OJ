package cn.edu.hqu.databackup.service.file;


import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: egret
 */

public interface ContestFileService {

    public void downloadContestRank(Long cid, Boolean forceRefresh, Boolean removeStar, HttpServletResponse response) throws StatusFailException, IOException, StatusForbiddenException;

    public void downloadContestACSubmission(Long cid, Boolean excludeAdmin, String splitType, HttpServletResponse response) throws StatusFailException, StatusForbiddenException;

    public void downloadContestPrintText(Long id, HttpServletResponse response) throws StatusForbiddenException;
}