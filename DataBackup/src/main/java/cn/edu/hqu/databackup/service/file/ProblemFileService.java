package cn.edu.hqu.databackup.service.file;

import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.result.CommonResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author egret
 */
public interface ProblemFileService {

    public CommonResult<Void> importProblem(MultipartFile file);

    public void exportProblem(List<Long> pidList, HttpServletResponse response);
}
