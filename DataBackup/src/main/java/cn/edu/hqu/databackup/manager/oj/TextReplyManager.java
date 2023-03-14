package cn.edu.hqu.databackup.manager.oj;

import cn.edu.hqu.api.pojo.entity.msg.TextMessage;
import cn.edu.hqu.databackup.common.constant.WechatMsgTypeConstant;
import cn.edu.hqu.databackup.utils.WechatMessageUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 文本回复
 *
 * @author egret
 */
@Component
@Slf4j(topic = "hoj")
public class TextReplyManager {

    private static final String FROM_USER_NAME = "FromUserName";
    private static final String TO_USER_NAME = "ToUserName";

    /**
     * 微信回复
     *
     * @param
     * @return
     * @throws UnsupportedEncodingException
     */
    public String callback(WxMpXmlMessage message) throws Exception {

        Map<String, String> requestMap = WechatMessageUtils.parseXml(message);
        // 消息类型
        String msgType = requestMap.get("MsgType");
        log.info("map:" + requestMap);
        log.info("msgType:" + msgType);
        // 处理其他消息，暂时不做回复
        switch (msgType) {
            case WechatMsgTypeConstant.MESSAGE_TYPE_EVENT: {
                // 回复授权信息
                log.info("回复授权信息");
                return reply(requestMap);
            }
            default: {
                // 不做回复
                log.info("不做回复");
                return null;
            }
        }
    }

    /**
     * 自动回复文本内容
     *
     * @param requestMap
     * @return
     */
    public String reply(Map<String, String> requestMap) throws UnsupportedEncodingException {
        String wechatId = requestMap.get(FROM_USER_NAME);
        String officialAccountId = requestMap.get(TO_USER_NAME);

        TextMessage textMessage = WechatMessageUtils.getDefaultTextMessage(wechatId, officialAccountId);

        String path = "https://ministudy.sciba.cn/api/invoke";
        path = URLEncoder.encode(path, "UTF-8");
        String href = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=wxa824cdd774f576bc" +
                "&redirect_uri=" + path +
                "&response_type=code" +
                "&scope=snsapi_userinfo" +
                "&state=nothing" +
                "#wechat_redirect" +
                "&forcePopup=true";
        textMessage.setContent("<a href=\"" + href + "\">授权注册！</a>");
        return WechatMessageUtils.textMessageToXml(textMessage);
    }
}
