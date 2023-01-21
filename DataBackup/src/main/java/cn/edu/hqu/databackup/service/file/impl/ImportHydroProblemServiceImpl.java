package cn.edu.hqu.databackup.service.file.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusSystemErrorException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.file.ImportHydroProblemManager;
import cn.edu.hqu.databackup.service.file.ImportHydroProblemService;

import javax.annotation.Resource;

/**
 * @Author: egret
 */
@Component
public class ImportHydroProblemServiceImpl implements ImportHydroProblemService {

    @Resource
    private ImportHydroProblemManager importHydroProblemManager;

    @Override
    public CommonResult<Void> importHydroProblem(MultipartFile file) {
        try {
            importHydroProblemManager.importHydroProblem(file);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }
}
