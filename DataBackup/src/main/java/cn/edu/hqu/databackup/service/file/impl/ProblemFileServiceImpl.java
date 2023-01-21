package cn.edu.hqu.databackup.service.file.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusSystemErrorException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.file.ProblemFileManager;
import cn.edu.hqu.databackup.service.file.ProblemFileService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: egret
 */
@Service
public class ProblemFileServiceImpl implements ProblemFileService {

    @Resource
    private ProblemFileManager problemFileManager;

    @Override
    public CommonResult<Void> importProblem(MultipartFile file) {
        try {
            problemFileManager.importProblem(file);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }

    @Override
    public void exportProblem(List<Long> pidList, HttpServletResponse response) {
        problemFileManager.exportProblem(pidList, response);
    }
}