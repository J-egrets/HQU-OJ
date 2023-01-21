package cn.edu.hqu.databackup.manager.admin.system;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.edu.hqu.databackup.dao.contest.ContestEntityService;
import cn.edu.hqu.databackup.dao.judge.JudgeEntityService;
import cn.edu.hqu.databackup.dao.user.SessionEntityService;
import cn.edu.hqu.databackup.dao.user.UserInfoEntityService;
import cn.edu.hqu.api.pojo.entity.user.Session;
import cn.edu.hqu.databackup.shiro.AccountProfile;

import java.util.List;
import java.util.Map;

/**
 * @Author: egret
 */
@Component
public class DashboardManager {

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private SessionEntityService sessionEntityService;

    public Session getRecentSession() {
        // 需要获取一下该token对应用户的数据
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        QueryWrapper<Session> wrapper = new QueryWrapper<Session>().eq("uid", userRolesVo.getUid()).orderByDesc("gmt_create");
        List<Session> sessionList = sessionEntityService.list(wrapper);
        if (sessionList.size() > 1) {
            return sessionList.get(1);
        } else {
            return sessionList.get(0);
        }
    }

    public Map<Object, Object> getDashboardInfo() {
        int userNum = userInfoEntityService.count();
        int recentContestNum = contestEntityService.getWithinNext14DaysContests().size();
        int todayJudgeNum = judgeEntityService.getTodayJudgeNum();
        return MapUtil.builder()
                .put("userNum", userNum)
                .put("recentContestNum", recentContestNum)
                .put("todayJudgeNum", todayJudgeNum).map();
    }
}