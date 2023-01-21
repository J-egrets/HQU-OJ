package cn.edu.hqu.databackup.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import cn.edu.hqu.api.pojo.entity.discussion.Reply;

/**
 * @Author: egret
 */
@Data
@Accessors(chain = true)
public class ReplyDTO {

    private Reply reply;

    private Integer did;

    private Integer quoteId;

    private String quoteType;
}