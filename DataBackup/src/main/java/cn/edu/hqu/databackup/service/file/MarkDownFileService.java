package cn.edu.hqu.databackup.service.file;

import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.result.CommonResult;

import java.util.Map;

/**
 * @author egret
 */
public interface MarkDownFileService {

    public CommonResult<Map<Object,Object>> uploadMDImg(MultipartFile image, Long gid);

    public CommonResult<Void> deleteMDImg(Long fileId);

    public CommonResult<Map<Object,Object>> uploadMd(MultipartFile file, Long gid);
}
