package cn.edu.hqu.databackup.service.file;

import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.result.CommonResult;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: egret
 */
public interface TestCaseService {

    public CommonResult<Map<Object, Object>> uploadTestcaseZip(MultipartFile file, Long gid, String mode);

    public void downloadTestcase(Long pid, HttpServletResponse response) throws StatusFailException, StatusForbiddenException;
}