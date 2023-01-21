package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: egret
 */

@Data
public class UserReadContestAnnouncementDTO {

    private Long cid;

    private List<Long> readAnnouncementList;
}