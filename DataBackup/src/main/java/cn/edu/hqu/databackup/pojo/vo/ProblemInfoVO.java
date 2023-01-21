package cn.edu.hqu.databackup.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.api.pojo.entity.problem.Tag;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: egret
 */
@Data
@AllArgsConstructor
public class ProblemInfoVO {
    /**
     * 题目内容
     */
    private Problem problem;
    /**
     * 题目标签
     */
    private List<Tag> tags;
    /**
     * 题目可用编程语言
     */
    private List<String> languages;
    /**
     * 题目提交统计情况
     */
    private ProblemCountVO problemCount;
    /**
     * 题目默认模板
     */
    private HashMap<String, String> codeTemplate;
}