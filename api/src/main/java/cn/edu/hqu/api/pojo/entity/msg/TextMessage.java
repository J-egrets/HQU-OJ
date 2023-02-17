package cn.edu.hqu.api.pojo.entity.msg;

import lombok.Data;

/**
 * @description 文本消息
 */
@Data
public class TextMessage extends BaseMessage {

    /**
     * 回复的消息内容
     */
    private String Content;

}
