package cn.edu.hqu.databackup.controller.file;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import cn.edu.hqu.databackup.service.file.UserFileService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: egret
 */
@Controller
@RequestMapping("/api/file")
public class UserFileController {

    @Autowired
    private UserFileService userFileService;

    @RequestMapping("/generate-user-excel")
    @RequiresAuthentication
    @RequiresRoles("root")
    public void generateUserExcel(@RequestParam("key") String key, HttpServletResponse response) throws IOException {
        userFileService.generateUserExcel(key, response);
    }

}