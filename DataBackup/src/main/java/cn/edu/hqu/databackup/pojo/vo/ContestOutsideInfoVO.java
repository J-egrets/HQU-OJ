package cn.edu.hqu.databackup.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.edu.hqu.api.pojo.entity.contest.ContestProblem;

import java.util.List;

/**
 * @Author: egret
 */
@ApiModel(value = "赛外排行榜所需的比赛信息，同时包括题目题号、气球颜色", description = "")
@Data
public class ContestOutsideInfoVO {

    @ApiModelProperty(value = "比赛信息")
    private ContestVO contest;

    @ApiModelProperty(value = "比赛题目信息列表")
    private List<ContestProblem> problemList;
}