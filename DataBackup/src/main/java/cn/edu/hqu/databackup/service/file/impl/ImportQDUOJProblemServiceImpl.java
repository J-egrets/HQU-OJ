package cn.edu.hqu.databackup.service.file.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusSystemErrorException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.file.ImportQDUOJProblemManager;
import cn.edu.hqu.databackup.service.file.ImportQDUOJProblemService;

/**
 * @Author: egret
 */
@Service
public class ImportQDUOJProblemServiceImpl implements ImportQDUOJProblemService {

    @Autowired
    private ImportQDUOJProblemManager importQDUOJProblemManager;

    @Override
    public CommonResult<Void> importQDOJProblem(MultipartFile file) {
        try {
            importQDUOJProblemManager.importQDOJProblem(file);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }
}