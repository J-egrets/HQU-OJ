package cn.edu.hqu.databackup.service.file.impl;

import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusSystemErrorException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.file.TestCaseManager;
import cn.edu.hqu.databackup.service.file.TestCaseService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: egret
 */
@Service
public class TestCaseServiceImpl implements TestCaseService {

    @Resource
    private TestCaseManager testCaseManager;

    @Override
    public CommonResult<Map<Object, Object>> uploadTestcaseZip(MultipartFile file, Long gid, String mode) {
        try {
            return CommonResult.successResponse(testCaseManager.uploadTestcaseZip(file, gid, mode));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public void downloadTestcase(Long pid, HttpServletResponse response) throws StatusFailException, StatusForbiddenException {
        testCaseManager.downloadTestcase(pid, response);
    }
}