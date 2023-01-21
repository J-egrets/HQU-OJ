package cn.edu.hqu.databackup.service.admin.tag;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.problem.Tag;
import cn.edu.hqu.api.pojo.entity.problem.TagClassification;

import java.util.List;

/**
 * @author egret
 */
public interface AdminTagService {

    public CommonResult<Tag> addTag(Tag tag);

    public CommonResult<Void> updateTag(Tag tag);

    public CommonResult<Void> deleteTag(Long tid);

    public CommonResult<List<TagClassification>> getTagClassification(String oj);

    public CommonResult<TagClassification> addTagClassification(TagClassification tagClassification);

    public CommonResult<Void> updateTagClassification(TagClassification tagClassification);

    public CommonResult<Void> deleteTagClassification(Long tcid);
}
