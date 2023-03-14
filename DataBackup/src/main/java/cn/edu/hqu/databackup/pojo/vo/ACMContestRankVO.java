package cn.edu.hqu.databackup.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * @Author: egret
 */
@Data
@Accessors(chain = true)
public class ACMContestRankVO {

    @ApiModelProperty(value = "排名,排名为-1则为打星队伍")
    private Integer rank;

    @ApiModelProperty(value = "是否得奖")
    private Boolean isWinAward;

    @ApiModelProperty(value = "排名奖项名称")
    private String awardName;

    @ApiModelProperty(value = "排名背景颜色")
    private String awardBackground;

    @ApiModelProperty(value = "排名文本颜色")
    private String awardColor;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户真实姓名")
    private String realname;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "学校")
    private String school;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "提交总罚时")
    private Long totalTime;

    @ApiModelProperty(value = "总提交数")
    private Integer total;

    @ApiModelProperty(value = "ac题目数")
    private Integer ac;

    @ApiModelProperty(value = "年级")
    private String grade;

    @ApiModelProperty(value = "专业")
    private String course;

    @ApiModelProperty(value = "学号")
    private String number;

    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    @ApiModelProperty(value = "有提交的题的提交详情")
    private HashMap<String,HashMap<String,Object>> submissionInfo;
}