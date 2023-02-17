package cn.edu.hqu.databackup.utils;

import cn.edu.hqu.api.pojo.entity.msg.TextMessage;
import cn.edu.hqu.databackup.common.constant.WechatMsgTypeConstant;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author C.W
 * @date 2022/5/18 7:55
 * @desc 微信消息
 */
public class WechatMessageUtils {

    /**
     * 解析微信发来的请求（XML）
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseXml(WxMpXmlMessage message) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<>();

        //request中有相应的信息，进行解析
        map.put("MsgType",message.getMsgType());
        map.put("FromUserName",message.getFromUser());
        map.put("ToUserName",message.getToUser());
        return map;
    }

    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage 文本消息对象
     * @return xml
     */
    public static String textMessageToXml(TextMessage textMessage) {
        XSTREAM.alias("xml", textMessage.getClass());
        return XSTREAM.toXML(textMessage);
    }

    /**
     * 音乐消息对象转换成xml
     *
     * @param musicMessage 音乐消息对象
     * @return xml
     */
//    public static String musicMessageToXml(MusicMessage musicMessage) {
//        XSTREAM.alias("xml", musicMessage.getClass());
//        return XSTREAM.toXML(musicMessage);
//    }

    /**
     * 图文消息对象转换成xml
     *
     * @param newsMessage 图文消息对象
     * @return xml
     */
//    public static String newsMessageToXml(NewsMessage newsMessage) {
//        XSTREAM.alias("xml", newsMessage.getClass());
//        XSTREAM.alias("item", Article.class);
//        return XSTREAM.toXML(newsMessage);
//    }

    /**
     * 扩展xstream，使其支持CDATA块
     */
    private static final XStream XSTREAM = new XStream(new XppDriver() {
        @Override
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                final boolean cdata = true;

                @Override
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });

    /**
     * 获取默认文本消息
     *
     * @param receiver     接收人
     * @param officialAccountId 官方微信id
     * @return 文本消息
     */
    public static TextMessage getDefaultTextMessage(String receiver, String officialAccountId) {
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(receiver);
        textMessage.setFromUserName(officialAccountId);
        textMessage.setCreateTime(System.currentTimeMillis());
        textMessage.setMsgType(WechatMsgTypeConstant.MESSAGE_TYPE_TEXT);
        textMessage.setFuncFlag(0);
        return textMessage;
    }

}
