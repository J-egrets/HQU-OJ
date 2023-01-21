package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;
import cn.edu.hqu.api.pojo.entity.common.Announcement;

import javax.validation.constraints.NotBlank;

/**
 * @Author: egret
 */
@Data
public class AnnouncementDTO {
    @NotBlank(message = "比赛id不能为空")
    private Long cid;

    private Announcement announcement;
}