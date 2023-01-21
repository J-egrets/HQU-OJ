package cn.edu.hqu.databackup.service.file.impl;

import org.springframework.stereotype.Service;
import cn.edu.hqu.databackup.manager.file.UserFileManager;
import cn.edu.hqu.databackup.service.file.UserFileService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: egret
 */
@Service
public class UserFileServiceImpl implements UserFileService {

    @Resource
    private UserFileManager userFileManager;


    @Override
    public void generateUserExcel(String key, HttpServletResponse response) throws IOException {
        userFileManager.generateUserExcel(key, response);
    }
}