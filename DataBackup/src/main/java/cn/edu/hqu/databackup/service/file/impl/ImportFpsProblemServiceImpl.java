package cn.edu.hqu.databackup.service.file.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.file.ImportFpsProblemManager;
import cn.edu.hqu.databackup.service.file.ImportFpsProblemService;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author: egret
 */
@Service
public class ImportFpsProblemServiceImpl implements ImportFpsProblemService {

    @Resource
    private ImportFpsProblemManager importFpsProblemManager;

    @Override
    public CommonResult<Void> importFPSProblem(MultipartFile file) {
        try {
            importFpsProblemManager.importFPSProblem(file);
            return CommonResult.successResponse();
        } catch (IOException | StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}