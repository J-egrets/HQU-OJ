package cn.edu.hqu.databackup.service.file;

import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.group.Group;

import java.util.Map;

/**
 * @author egret
 */
public interface ImageService {

    public CommonResult<Map<Object, Object>> uploadAvatar(MultipartFile image);

    public CommonResult<Group> uploadGroupAvatar(MultipartFile image, Long gid);

    public CommonResult<Map<Object, Object>> uploadCarouselImg(MultipartFile image);
}
