package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;

/**
 * @Author: egret
 */
@Data
public class LastAcceptedCodeVO {

    private Long submitId;

    private String code;

    private String language;
}
