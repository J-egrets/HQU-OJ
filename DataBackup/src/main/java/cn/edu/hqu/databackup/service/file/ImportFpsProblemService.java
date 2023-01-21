package cn.edu.hqu.databackup.service.file;

import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.result.CommonResult;

/**
 * @Author: egret
 */
public interface ImportFpsProblemService {

    public CommonResult<Void> importFPSProblem(MultipartFile file);
}