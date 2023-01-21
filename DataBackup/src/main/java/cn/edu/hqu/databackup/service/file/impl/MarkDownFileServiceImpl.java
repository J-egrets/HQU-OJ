package cn.edu.hqu.databackup.service.file.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusSystemErrorException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.file.MarkDownFileManager;
import cn.edu.hqu.databackup.service.file.MarkDownFileService;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author: egret
 */
@Service
public class MarkDownFileServiceImpl implements MarkDownFileService {

    @Resource
    private MarkDownFileManager markDownFileManager;

    @Override
    public CommonResult<Map<Object, Object>> uploadMDImg(MultipartFile image, Long gid) {
        try {
            return CommonResult.successResponse(markDownFileManager.uploadMDImg(image, gid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> deleteMDImg(Long fileId) {
        try {
            markDownFileManager.deleteMDImg(fileId);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Map<Object, Object>> uploadMd(MultipartFile file, Long gid) {
        try {
            return CommonResult.successResponse(markDownFileManager.uploadMd(file, gid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }
}