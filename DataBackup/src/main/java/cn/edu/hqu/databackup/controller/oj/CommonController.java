package cn.edu.hqu.databackup.controller.oj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.edu.hqu.databackup.annotation.AnonApi;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.api.pojo.entity.problem.CodeTemplate;
import cn.edu.hqu.api.pojo.entity.problem.Language;
import cn.edu.hqu.api.pojo.entity.problem.Tag;
import cn.edu.hqu.api.pojo.entity.training.TrainingCategory;
import cn.edu.hqu.databackup.pojo.vo.CaptchaVO;
import cn.edu.hqu.databackup.pojo.vo.ProblemTagVO;
import cn.edu.hqu.databackup.service.oj.CommonService;

import java.util.Collection;
import java.util.List;

/**
 * @Author: egret
 * @Description: 通用的请求控制处理类
 */
@RestController
@RequestMapping("/api")
public class CommonController {

    @Autowired
    private CommonService commonService;


    @GetMapping("/captcha")
    @AnonApi
    public CommonResult<CaptchaVO> getCaptcha() {
        return commonService.getCaptcha();
    }


    @GetMapping("/get-training-category")
    @AnonApi
    public CommonResult<List<TrainingCategory>> getTrainingCategory() {
        return commonService.getTrainingCategory();
    }

    @GetMapping("/get-all-problem-tags")
    @AnonApi
    public CommonResult<List<Tag>> getAllProblemTagsList(@RequestParam(value = "oj", defaultValue = "ME") String oj) {
        return commonService.getAllProblemTagsList(oj);
    }

    @GetMapping("/get-problem-tags-and-classification")
    @AnonApi
    public CommonResult<List<ProblemTagVO>> getProblemTagsAndClassification(@RequestParam(value = "oj", defaultValue = "ME") String oj) {
        return commonService.getProblemTagsAndClassification(oj);
    }

    @GetMapping("/get-problem-tags")
    @AnonApi
    public CommonResult<Collection<Tag>> getProblemTags(Long pid) {
        return commonService.getProblemTags(pid);
    }


    @GetMapping("/languages")
    @AnonApi
    public CommonResult<List<Language>> getLanguages(@RequestParam(value = "pid", required = false) Long pid,
                                                     @RequestParam(value = "all", required = false) Boolean all) {
        return commonService.getLanguages(pid, all);
    }

    @GetMapping("/get-problem-languages")
    @AnonApi
    public CommonResult<Collection<Language>> getProblemLanguages(@RequestParam("pid") Long pid) {
        return commonService.getProblemLanguages(pid);
    }

    @GetMapping("/get-problem-code-template")
    @AnonApi
    public CommonResult<List<CodeTemplate>> getProblemCodeTemplate(@RequestParam("pid") Long pid) {
        return commonService.getProblemCodeTemplate(pid);
    }

}