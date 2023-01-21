package cn.edu.hqu.databackup.service.file;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author egret
 */
public interface UserFileService {

    public void generateUserExcel(String key, HttpServletResponse response) throws IOException;
}
