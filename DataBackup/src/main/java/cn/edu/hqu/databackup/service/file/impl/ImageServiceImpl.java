package cn.edu.hqu.databackup.service.file.impl;

import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusSystemErrorException;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.manager.file.ImageManager;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.databackup.service.file.ImageService;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author: egret
 */
@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    private ImageManager imageManager;

    @Override
    public CommonResult<Map<Object, Object>> uploadAvatar(MultipartFile image) {
        try {
            return CommonResult.successResponse(imageManager.uploadAvatar(image));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }

    @Override
    public CommonResult<Group> uploadGroupAvatar(MultipartFile image, Long gid) {
        try {
            return CommonResult.successResponse(imageManager.uploadGroupAvatar(image, gid));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Map<Object, Object>> uploadCarouselImg(MultipartFile image) {
        try {
            return CommonResult.successResponse(imageManager.uploadCarouselImg(image));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }
}