package cn.edu.hqu.databackup.controller.oj;

import cn.edu.hqu.databackup.annotation.AnonApi;
import cn.edu.hqu.databackup.common.result.CommonResult;
import cn.edu.hqu.databackup.pojo.dto.WxBindDTO;
import cn.edu.hqu.databackup.pojo.dto.WxLoginDTO;
import cn.edu.hqu.databackup.pojo.vo.UserInfoVO;
import cn.edu.hqu.databackup.service.oj.PassportService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信登录相关控制器
 * @author egret
 */
@RestController
public class WXController {

    @Autowired
    private PassportService passportService;

    /**
     * 获取二维码
     * @return
     */
    @GetMapping("/api/getQrCode")
    @AnonApi
    public CommonResult<JSONObject> getQrCode(@RequestParam("bind") Boolean bind){
        return passportService.getQrCode(bind);
    }

    /**
     * 验证签名，扫码后回调验证签名
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/api/callback")
    @AnonApi
    public String checkSign (HttpServletRequest request){
        return passportService.checkSign(request);
    }

    @RequestMapping("/api/invoke")
    @AnonApi
    public String oauthInvoke(HttpServletRequest request){
        return passportService.oauthInvoke(request);
    }

    /**
     * 根据二维码标识获取用户openId=>获取用户信息
     * @param wxLoginDTO
     * @return
     */
    @RequestMapping("/api/wxLogin")
    @AnonApi
    public CommonResult<UserInfoVO> wxLogin(@Validated @RequestBody WxLoginDTO wxLoginDTO, HttpServletResponse response, HttpServletRequest request){
        return passportService.wxLogin(wxLoginDTO,response,request);
    }

    /**
     * 检测微信绑定
     * @param wxBindDTO
     * @return
     */
    @RequestMapping("/api/checkWxBind")
    public CommonResult<UserInfoVO> checkWxBind(@Validated @RequestBody WxBindDTO wxBindDTO){
        return passportService.checkWxBind(wxBindDTO);
    }

    /**
     * 文本校验
     * @param response
     * @return
     */
    @GetMapping({"/MP_verify_8bifHVnP0gajOnw5.txt"})
    @AnonApi
    public String returnConfigFile(HttpServletResponse response) {
        //把MP_verify_8bifHVnP0gajOnw5.txt中的内容返回
        return "8bifHVnP0gajOnw5";
    }
}
