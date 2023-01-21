package cn.edu.hqu.databackup.utils;

import org.apache.shiro.SecurityUtils;
import cn.edu.hqu.databackup.shiro.AccountProfile;

/**
 * @Author: egret
 */
public class ShiroUtils {

    private ShiroUtils() {
    }

    public static AccountProfile getProfile(){
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

}