package cn.edu.hqu.databackup.service.oj.impl;

import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.manager.oj.CommonManager;
import cn.edu.hqu.api.pojo.entity.problem.CodeTemplate;
import cn.edu.hqu.api.pojo.entity.problem.Language;
import cn.edu.hqu.api.pojo.entity.problem.Tag;
import cn.edu.hqu.api.pojo.entity.training.TrainingCategory;
import cn.edu.hqu.databackup.pojo.vo.CaptchaVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemTagVO;
import cn.edu.hqu.databackup.service.oj.CommonService;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @Author: egret
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Resource
    private CommonManager commonManager;

    @Override
    public CommonResult<CaptchaVO> getCaptcha() {
        return CommonResult.successResponse(commonManager.getCaptcha());
    }

    @Override
    public CommonResult<List<TrainingCategory>> getTrainingCategory() {
        return CommonResult.successResponse(commonManager.getTrainingCategory());
    }

    @Override
    public CommonResult<List<Tag>> getAllProblemTagsList(String oj) {
        return CommonResult.successResponse(commonManager.getAllProblemTagsList(oj));
    }

    @Override
    public CommonResult<List<ProblemTagVO>> getProblemTagsAndClassification(String oj) {
        return CommonResult.successResponse(commonManager.getProblemTagsAndClassification(oj));
    }

    @Override
    public CommonResult<Collection<Tag>> getProblemTags(Long pid) {
        return CommonResult.successResponse(commonManager.getProblemTags(pid));
    }

    @Override
    public CommonResult<List<Language>> getLanguages(Long pid, Boolean all) {
        return CommonResult.successResponse(commonManager.getLanguages(pid, all));
    }

    @Override
    public CommonResult<Collection<Language>> getProblemLanguages(Long pid) {
        return CommonResult.successResponse(commonManager.getProblemLanguages(pid));
    }

    @Override
    public CommonResult<List<CodeTemplate>> getProblemCodeTemplate(Long pid) {
        return CommonResult.successResponse(commonManager.getProblemCodeTemplate(pid));
    }
}