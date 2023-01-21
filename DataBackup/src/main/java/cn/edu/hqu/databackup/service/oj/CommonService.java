package cn.edu.hqu.databackup.service.oj;

import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.problem.CodeTemplate;
import cn.edu.hqu.api.pojo.entity.problem.Language;
import cn.edu.hqu.api.pojo.entity.problem.Tag;
import cn.edu.hqu.api.pojo.entity.training.TrainingCategory;
import cn.edu.hqu.databackup.pojo.vo.CaptchaVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemTagVO;

import java.util.Collection;
import java.util.List;

/**
 * @author egret
 */
public interface CommonService {

    public CommonResult<CaptchaVO> getCaptcha();

    public CommonResult<List<TrainingCategory>> getTrainingCategory();

    public CommonResult<List<Tag>> getAllProblemTagsList(String oj);

    public CommonResult<List<ProblemTagVO>> getProblemTagsAndClassification(String oj);

    public CommonResult<Collection<Tag>> getProblemTags(Long pid);

    public CommonResult<List<Language>> getLanguages(Long pid, Boolean all);

    public CommonResult<Collection<Language>> getProblemLanguages(Long pid);

    public CommonResult<List<CodeTemplate>> getProblemCodeTemplate(Long pid);
}
