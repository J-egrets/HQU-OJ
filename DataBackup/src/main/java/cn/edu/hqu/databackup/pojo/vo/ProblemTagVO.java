package cn.edu.hqu.databackup.pojo.vo;

import lombok.Data;
import cn.edu.hqu.api.pojo.entity.problem.Tag;
import cn.edu.hqu.api.pojo.entity.problem.TagClassification;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: egret
 */
@Data
public class ProblemTagVO implements Serializable {
    /**
     * 标签分类
     */
    private TagClassification classification;

    /**
     * 标签列表
     */
    private List<Tag> tagList;

}
