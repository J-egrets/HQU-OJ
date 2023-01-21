package cn.edu.hqu.databackup.service.file;

import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.result.CommonResult;

/**
 * @author egret
 */
public interface ImportQDUOJProblemService {

    public CommonResult<Void> importQDOJProblem(MultipartFile file);
}
