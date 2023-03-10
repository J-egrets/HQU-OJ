package cn.edu.hqu.databackup.manager.oj;

import cn.edu.hqu.api.pojo.entity.user.UserInfo;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.dao.contest.ContestRecordEntityService;
import cn.edu.hqu.databackup.dao.group.GroupMemberEntityService;
import cn.edu.hqu.databackup.dao.user.UserInfoEntityService;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.group.GroupMember;
import cn.edu.hqu.databackup.pojo.vo.ACMContestRankVO;
import cn.edu.hqu.databackup.pojo.vo.ContestAwardConfigVO;
import cn.edu.hqu.databackup.pojo.vo.ContestRecordVO;
import cn.edu.hqu.databackup.pojo.vo.OIContestRankVO;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.utils.RedisUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Component
public class ContestCalculateRankManager {

    @Resource
    private UserInfoEntityService userInfoEntityService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private ContestRecordEntityService contestRecordEntityService;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    public List<ACMContestRankVO> calcACMRank(boolean isOpenSealRank,
                                              boolean removeStar,
                                              Contest contest,
                                              String currentUserId,
                                              List<String> concernedList,
                                              List<Integer> externalCidList) {
        return calcACMRank(isOpenSealRank,
                removeStar,
                contest,
                currentUserId,
                concernedList,
                externalCidList,
                false,
                null);
    }


    public List<OIContestRankVO> calcOIRank(Boolean isOpenSealRank,
                                            Boolean removeStar,
                                            Contest contest,
                                            String currentUserId,
                                            List<String> concernedList,
                                            List<Integer> externalCidList) {

        return calcOIRank(isOpenSealRank,
                removeStar,
                contest,
                currentUserId,
                concernedList,
                externalCidList,
                false,
                null);
    }


    /**
     * @param isOpenSealRank  ?????????????????????????????????
     * @param removeStar      ??????????????????????????????
     * @param contest         ??????????????????
     * @param currentUserId   ???????????????????????????uuid,??????????????????????????????????????????????????????
     * @param concernedList   ??????????????????uuid?????????
     * @param externalCidList ?????????????????????????????????
     * @param useCache        ????????????????????????????????????????????????
     * @param cacheTime       ??????????????? ?????????
     * @MethodName calcACMRank
     * @Description TODO
     * @Return
     */
    public List<ACMContestRankVO> calcACMRank(boolean isOpenSealRank,
                                              boolean removeStar,
                                              Contest contest,
                                              String currentUserId,
                                              List<String> concernedList,
                                              List<Integer> externalCidList,
                                              boolean useCache,
                                              Long cacheTime) {
        List<ACMContestRankVO> orderResultList;
        if (useCache) {
            String key = Constants.Contest.CONTEST_RANK_CAL_RESULT_CACHE.getName() + "_" + contest.getId();
            orderResultList = (List<ACMContestRankVO>) redisUtils.get(key);
            if (orderResultList == null) {
                if (isOpenSealRank) {
                    long minSealRankTime = DateUtil.between(contest.getStartTime(), contest.getSealRankTime(), DateUnit.SECOND);
                    orderResultList = getACMOrderRank(contest, true, minSealRankTime, contest.getDuration(), externalCidList);
                } else {
                    orderResultList = getACMOrderRank(contest, false, null, null, externalCidList);
                }
                redisUtils.set(key, orderResultList, cacheTime);
            }
        } else {
            if (isOpenSealRank) {
                long minSealRankTime = DateUtil.between(contest.getStartTime(), contest.getSealRankTime(), DateUnit.SECOND);
                orderResultList = getACMOrderRank(contest, true, minSealRankTime, contest.getDuration(), externalCidList);
            } else {
                orderResultList = getACMOrderRank(contest, false, null, null, externalCidList);
            }
        }

        // ??????????????????????????????
        HashMap<String, Boolean> starAccountMap = starAccountToMap(contest.getStarAccount());

        Queue<ContestAwardConfigVO> awardConfigVoList = null;
        boolean isNeedSetAward = contest.getAwardType() != null && contest.getAwardType() > 0;
        if (removeStar) {
            // ???????????????????????????????????????????????????????????????????????????????????????
            orderResultList.removeIf(acmContestRankVo -> starAccountMap.containsKey(acmContestRankVo.getUsername()));
            if (isNeedSetAward) {
                awardConfigVoList = getContestAwardConfigList(contest.getAwardConfig(), contest.getAwardType(), orderResultList.size());
            }
        } else {
            if (isNeedSetAward) {
                if (contest.getAwardType() == 1) {
                    long count = orderResultList.stream().filter(e -> !starAccountMap.containsKey(e.getUsername())).count();
                    awardConfigVoList = getContestAwardConfigList(contest.getAwardConfig(), contest.getAwardType(), (int) count);
                } else {
                    awardConfigVoList = getContestAwardConfigList(contest.getAwardConfig(), contest.getAwardType(), orderResultList.size());
                }
            }
        }
        // ??????????????????????????????????????????????????????????????????
        List<ACMContestRankVO> topACMRankVoList = new ArrayList<>();
        boolean needAddConcernedUser = false;
        if (!CollectionUtils.isEmpty(concernedList)) {
            needAddConcernedUser = true;
            // ???????????????????????????????????????
            concernedList.remove(currentUserId);
        }

        int rankNum = 1;
        int len = orderResultList.size();
        ACMContestRankVO lastACMRankVo = null;
        ContestAwardConfigVO configVo = null;
        for (int i = 0; i < len; i++) {
            ACMContestRankVO currentACMRankVo = orderResultList.get(i);
            if (!removeStar && starAccountMap.containsKey(currentACMRankVo.getUsername())) {
                // ?????????????????????-1
                currentACMRankVo.setRank(-1);
                currentACMRankVo.setIsWinAward(false);
            } else {
                if (rankNum == 1) {
                    currentACMRankVo.setRank(rankNum);
                } else {
                    // ???????????????????????????AC???????????????????????????????????????????????????????????????????????????????????????
                    if (Objects.equals(lastACMRankVo.getAc(), currentACMRankVo.getAc())
                            && lastACMRankVo.getTotalTime().equals(currentACMRankVo.getTotalTime())) {
                        currentACMRankVo.setRank(lastACMRankVo.getRank());
                    } else {
                        currentACMRankVo.setRank(rankNum);
                    }
                }

                if (isNeedSetAward && currentACMRankVo.getAc() > 0) {
                    if (configVo == null || configVo.getNum() == 0) {
                        if (!awardConfigVoList.isEmpty()) {
                            configVo = awardConfigVoList.poll();
                            currentACMRankVo.setAwardName(configVo.getName());
                            currentACMRankVo.setAwardBackground(configVo.getBackground());
                            currentACMRankVo.setAwardColor(configVo.getColor());
                            currentACMRankVo.setIsWinAward(true);
                            configVo.setNum(configVo.getNum() - 1);
                        } else {
                            isNeedSetAward = false;
                            currentACMRankVo.setIsWinAward(false);
                        }
                    } else {
                        currentACMRankVo.setAwardName(configVo.getName());
                        currentACMRankVo.setAwardBackground(configVo.getBackground());
                        currentACMRankVo.setAwardColor(configVo.getColor());
                        currentACMRankVo.setIsWinAward(true);
                        configVo.setNum(configVo.getNum() - 1);
                    }
                } else {
                    currentACMRankVo.setIsWinAward(false);
                }

                lastACMRankVo = currentACMRankVo;
                rankNum++;
            }
            // ??????????????????????????????????????????
            if (!StringUtils.isEmpty(currentUserId) &&
                    currentACMRankVo.getUid().equals(currentUserId)) {
                topACMRankVoList.add(0, currentACMRankVo);
            }

            // ????????????????????????
            if (needAddConcernedUser) {
                if (concernedList.contains(currentACMRankVo.getUid())) {
                    topACMRankVoList.add(currentACMRankVo);
                }
            }
        }
        topACMRankVoList.addAll(orderResultList);
        return topACMRankVoList;
    }


    private List<ACMContestRankVO> getACMOrderRank(Contest contest,
                                                   Boolean isOpenSealRank,
                                                   Long minSealRankTime,
                                                   Long maxSealRankTime,
                                                   List<Integer> externalCidList) {


        List<ContestRecordVO> contestRecordList = contestRecordEntityService.getACMContestRecord(contest.getUid(),
                contest.getId(),
                externalCidList,
                contest.getStartTime());

        List<String> superAdminUidList = getSuperAdminUidList(contest.getGid());

        List<ACMContestRankVO> result = new ArrayList<>();

        HashMap<String, Integer> uidMapIndex = new HashMap<>();

        int index = 0;

        HashMap<String, Long> firstACMap = new HashMap<>();

        for (ContestRecordVO contestRecord : contestRecordList) {

            // ???????????????????????????????????????
            if (superAdminUidList.contains(contestRecord.getUid())) {
                continue;
            }

            ACMContestRankVO ACMContestRankVo;
            // ?????????????????????????????????
            if (!uidMapIndex.containsKey(contestRecord.getUid())) {

                UserInfo userInfo = userInfoEntityService.getUserInfo(contestRecord.getUid());
                // ???????????????
                ACMContestRankVo = new ACMContestRankVO();
                ACMContestRankVo.setRealname(contestRecord.getRealname())
                        .setAvatar(contestRecord.getAvatar())
                        .setSchool(contestRecord.getSchool())
                        .setGender(contestRecord.getGender())
                        .setUid(contestRecord.getUid())
                        .setUsername(contestRecord.getUsername())
                        .setNickname(contestRecord.getNickname())
                        .setAc(0)
                        .setTotalTime(0L)
                        .setTotal(0)
                        .setGrade(userInfo.getGrade())
                        .setCourse(userInfo.getCourse())
                        .setNumber(userInfo.getNumber())
                        .setPhoneNumber(userInfo.getPhoneNumber());

                HashMap<String, HashMap<String, Object>> submissionInfo = new HashMap<>();
                ACMContestRankVo.setSubmissionInfo(submissionInfo);

                result.add(ACMContestRankVo);
                uidMapIndex.put(contestRecord.getUid(), index);
                index++;
            } else {
                ACMContestRankVo = result.get(uidMapIndex.get(contestRecord.getUid())); // ???????????????index????????????
            }

            HashMap<String, Object> problemSubmissionInfo = ACMContestRankVo.getSubmissionInfo().get(contestRecord.getDisplayId());

            if (problemSubmissionInfo == null) {
                problemSubmissionInfo = new HashMap<>();
                problemSubmissionInfo.put("errorNum", 0);
            }

            ACMContestRankVo.setTotal(ACMContestRankVo.getTotal() + 1);

            // ?????????????????????????????????????????????????????????????????????????????? ????????????+1
            if (isOpenSealRank && isInSealTimeSubmission(minSealRankTime, maxSealRankTime, contestRecord.getTime())) {

                int tryNum = (int) problemSubmissionInfo.getOrDefault("tryNum", 0);
                problemSubmissionInfo.put("tryNum", tryNum + 1);

            } else {

                // ?????????????????????AC??????????????????????????????
                if ((Boolean) problemSubmissionInfo.getOrDefault("isAC", false)) {
                    continue;
                }

                // ?????????????????????????????????time?????????

                // ????????????
                if (contestRecord.getStatus().intValue() == Constants.Contest.RECORD_AC.getCode()) {
                    // ?????????????????????ac+1
                    ACMContestRankVo.setAc(ACMContestRankVo.getAc() + 1);

                    // ???????????????first AC
                    boolean isFirstAC = false;
                    Long time = firstACMap.getOrDefault(contestRecord.getDisplayId(), null);
                    if (time == null) {
                        isFirstAC = true;
                        firstACMap.put(contestRecord.getDisplayId(), contestRecord.getTime());
                    } else {
                        // ????????????????????????first AC
                        if (time.longValue() == contestRecord.getTime().longValue()) {
                            isFirstAC = true;
                        }
                    }

                    int errorNumber = (int) problemSubmissionInfo.getOrDefault("errorNum", 0);
                    problemSubmissionInfo.put("isAC", true);
                    problemSubmissionInfo.put("isFirstAC", isFirstAC);
                    problemSubmissionInfo.put("ACTime", contestRecord.getTime());
                    problemSubmissionInfo.put("errorNum", errorNumber);

                    // ??????????????????????????????????????? ????????????AC??????????????????*20*60+??????AC??????
                    ACMContestRankVo.setTotalTime(ACMContestRankVo.getTotalTime() + errorNumber * 20 * 60 + contestRecord.getTime());

                    // ???????????????????????????????????????
                } else if (contestRecord.getStatus().intValue() == Constants.Contest.RECORD_NOT_AC_PENALTY.getCode()) {

                    int errorNumber = (int) problemSubmissionInfo.getOrDefault("errorNum", 0);
                    problemSubmissionInfo.put("errorNum", errorNumber + 1);
                } else {

                    int errorNumber = (int) problemSubmissionInfo.getOrDefault("errorNum", 0);
                    problemSubmissionInfo.put("errorNum", errorNumber);
                }
            }
            ACMContestRankVo.getSubmissionInfo().put(contestRecord.getDisplayId(), problemSubmissionInfo);
        }

        List<ACMContestRankVO> orderResultList = result.stream().sorted(Comparator.comparing(ACMContestRankVO::getAc, Comparator.reverseOrder()) // ?????????ac?????????
                .thenComparing(ACMContestRankVO::getTotalTime) //?????????????????????
        ).collect(Collectors.toList());

        return orderResultList;
    }


    /**
     * @param isOpenSealRank  ?????????????????????????????????
     * @param removeStar      ??????????????????????????????
     * @param contest         ??????????????????
     * @param currentUserId   ???????????????????????????uuid,??????????????????????????????????????????????????????
     * @param concernedList   ??????????????????uuid?????????
     * @param externalCidList ??????????????????????????????
     * @param useCache        ????????????????????????????????????????????????
     * @param cacheTime       ??????????????? ?????????
     * @MethodName calcOIRank
     * @Description TODO
     * @Return
     */
    public List<OIContestRankVO> calcOIRank(boolean isOpenSealRank,
                                            boolean removeStar,
                                            Contest contest,
                                            String currentUserId,
                                            List<String> concernedList,
                                            List<Integer> externalCidList,
                                            boolean useCache,
                                            Long cacheTime) {

        List<OIContestRankVO> orderResultList;
        if (useCache) {
            String key = Constants.Contest.CONTEST_RANK_CAL_RESULT_CACHE.getName() + "_" + contest.getId();
            orderResultList = (List<OIContestRankVO>) redisUtils.get(key);
            if (orderResultList == null) {
                orderResultList = getOIOrderRank(contest, externalCidList, isOpenSealRank);
                redisUtils.set(key, orderResultList, cacheTime);
            }
        } else {
            orderResultList = getOIOrderRank(contest, externalCidList, isOpenSealRank);
        }

        // ??????????????????????????????
        HashMap<String, Boolean> starAccountMap = starAccountToMap(contest.getStarAccount());

        Queue<ContestAwardConfigVO> awardConfigVoList = null;
        boolean isNeedSetAward = contest.getAwardType() != null && contest.getAwardType() > 0;
        if (removeStar) {
            // ???????????????????????????????????????????????????????????????????????????????????????
            orderResultList.removeIf(acmContestRankVo -> starAccountMap.containsKey(acmContestRankVo.getUsername()));
            if (isNeedSetAward) {
                awardConfigVoList = getContestAwardConfigList(contest.getAwardConfig(), contest.getAwardType(), orderResultList.size());
            }
        } else {
            if (isNeedSetAward) {
                if (contest.getAwardType() == 1) {
                    long count = orderResultList.stream().filter(e -> !starAccountMap.containsKey(e.getUsername())).count();
                    awardConfigVoList = getContestAwardConfigList(contest.getAwardConfig(), contest.getAwardType(), (int) count);
                } else {
                    awardConfigVoList = getContestAwardConfigList(contest.getAwardConfig(), contest.getAwardType(), orderResultList.size());
                }
            }
        }

        // ??????????????????????????????????????????????????????????????????
        List<OIContestRankVO> topOIRankVoList = new ArrayList<>();
        boolean needAddConcernedUser = false;
        if (!CollectionUtils.isEmpty(concernedList)) {
            needAddConcernedUser = true;
            // ???????????????????????????????????????
            concernedList.remove(currentUserId);
        }

        int rankNum = 1;
        OIContestRankVO lastOIRankVo = null;
        ContestAwardConfigVO configVo = null;
        int len = orderResultList.size();
        for (int i = 0; i < len; i++) {
            OIContestRankVO currentOIRankVo = orderResultList.get(i);
            if (!removeStar && starAccountMap.containsKey(currentOIRankVo.getUsername())) {
                // ?????????????????????-1
                currentOIRankVo.setRank(-1);
                currentOIRankVo.setIsWinAward(false);
            } else {
                if (rankNum == 1) {
                    currentOIRankVo.setRank(rankNum);
                } else {
                    // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (lastOIRankVo.getTotalScore().equals(currentOIRankVo.getTotalScore())
                            && lastOIRankVo.getTotalTime().equals(currentOIRankVo.getTotalTime())) {
                        currentOIRankVo.setRank(lastOIRankVo.getRank());
                    } else {
                        currentOIRankVo.setRank(rankNum);
                    }
                }

                if (isNeedSetAward && currentOIRankVo.getTotalScore() > 0) {
                    if (configVo == null || configVo.getNum() == 0) {
                        if (!awardConfigVoList.isEmpty()) {
                            configVo = awardConfigVoList.poll();
                            currentOIRankVo.setAwardName(configVo.getName());
                            currentOIRankVo.setAwardBackground(configVo.getBackground());
                            currentOIRankVo.setAwardColor(configVo.getColor());
                            currentOIRankVo.setIsWinAward(true);
                            configVo.setNum(configVo.getNum() - 1);
                        } else {
                            isNeedSetAward = false;
                            currentOIRankVo.setIsWinAward(false);
                        }
                    } else {
                        currentOIRankVo.setAwardName(configVo.getName());
                        currentOIRankVo.setAwardBackground(configVo.getBackground());
                        currentOIRankVo.setAwardColor(configVo.getColor());
                        currentOIRankVo.setIsWinAward(true);
                        configVo.setNum(configVo.getNum() - 1);
                    }
                } else {
                    currentOIRankVo.setIsWinAward(false);
                }

                lastOIRankVo = currentOIRankVo;
                rankNum++;
            }

            // ???????????????????????????????????????????????????
            if (!StringUtils.isEmpty(currentUserId) &&
                    currentOIRankVo.getUid().equals(currentUserId)) {
                topOIRankVoList.add(0, currentOIRankVo);
            }

            // ????????????????????????
            if (needAddConcernedUser) {
                if (concernedList.contains(currentOIRankVo.getUid())) {
                    topOIRankVoList.add(currentOIRankVo);
                }
            }
        }
        topOIRankVoList.addAll(orderResultList);
        return topOIRankVoList;
    }

    private List<OIContestRankVO> getOIOrderRank(Contest contest, List<Integer> externalCidList, Boolean isOpenSealRank) {

        List<ContestRecordVO> oiContestRecord = contestRecordEntityService.getOIContestRecord(contest, externalCidList, isOpenSealRank);

        List<String> superAdminUidList = getSuperAdminUidList(contest.getGid());

        List<OIContestRankVO> result = new ArrayList<>();

        HashMap<String, Integer> uidMapIndex = new HashMap<>();

        HashMap<String, HashMap<String, Integer>> uidMapTime = new HashMap<>();

        boolean isHighestRankScore = Constants.Contest.OI_RANK_HIGHEST_SCORE.getName().equals(contest.getOiRankScoreType());

        int index = 0;

        for (ContestRecordVO contestRecord : oiContestRecord) {

            if (superAdminUidList.contains(contestRecord.getUid())) { // ???????????????????????????????????????
                continue;
            }

            if (contestRecord.getStatus().equals(Constants.Contest.RECORD_AC.getCode())) { // AC
                HashMap<String, Integer> pidMapTime = uidMapTime.get(contestRecord.getUid());
                if (pidMapTime != null) {
                    Integer useTime = pidMapTime.get(contestRecord.getDisplayId());
                    if (useTime != null) {
                        if (useTime > contestRecord.getUseTime()) {  // ?????????????????????????????????
                            pidMapTime.put(contestRecord.getDisplayId(), contestRecord.getUseTime());
                        }
                    } else {
                        pidMapTime.put(contestRecord.getDisplayId(), contestRecord.getUseTime());
                    }
                } else {
                    HashMap<String, Integer> tmp = new HashMap<>();
                    tmp.put(contestRecord.getDisplayId(), contestRecord.getUseTime());
                    uidMapTime.put(contestRecord.getUid(), tmp);
                }
            }

            OIContestRankVO oiContestRankVo;
            // ?????????????????????????????????
            if (!uidMapIndex.containsKey(contestRecord.getUid())) {
                UserInfo userInfo = userInfoEntityService.getUserInfo(contestRecord.getUid());
                // ???????????????
                oiContestRankVo = new OIContestRankVO();
                oiContestRankVo.setRealname(contestRecord.getRealname())
                        .setUid(contestRecord.getUid())
                        .setUsername(contestRecord.getUsername())
                        .setSchool(contestRecord.getSchool())
                        .setAvatar(contestRecord.getAvatar())
                        .setGender(contestRecord.getGender())
                        .setNickname(contestRecord.getNickname())
                        .setTotalScore(0)
                        .setGrade(userInfo.getGrade())
                        .setCourse(userInfo.getCourse())
                        .setNumber(userInfo.getNumber())
                        .setPhoneNumber(userInfo.getPhoneNumber());


                HashMap<String, Integer> submissionInfo = new HashMap<>();
                oiContestRankVo.setSubmissionInfo(submissionInfo);

                result.add(oiContestRankVo);
                uidMapIndex.put(contestRecord.getUid(), index);
                index++;
            } else {
                oiContestRankVo = result.get(uidMapIndex.get(contestRecord.getUid())); // ???????????????index????????????
            }

            // ????????????
            HashMap<String, Integer> submissionInfo = oiContestRankVo.getSubmissionInfo();
            Integer score = submissionInfo.get(contestRecord.getDisplayId());
            if (isHighestRankScore) {
                if (score == null) {
                    oiContestRankVo.setTotalScore(oiContestRankVo.getTotalScore() + contestRecord.getScore());
                    submissionInfo.put(contestRecord.getDisplayId(), contestRecord.getScore());
                }
            } else {
                if (contestRecord.getScore() != null) {
                    if (score != null) { // ?????????????????????????????????????????????
                        oiContestRankVo.setTotalScore(oiContestRankVo.getTotalScore() - score + contestRecord.getScore());
                    } else {
                        oiContestRankVo.setTotalScore(oiContestRankVo.getTotalScore() + contestRecord.getScore());
                    }
                }
                submissionInfo.put(contestRecord.getDisplayId(), contestRecord.getScore());
            }

        }


        for (OIContestRankVO oiContestRankVo : result) {
            HashMap<String, Integer> pidMapTime = uidMapTime.get(oiContestRankVo.getUid());
            int sumTime = 0;
            if (pidMapTime != null) {
                for (String key : pidMapTime.keySet()) {
                    Integer time = pidMapTime.get(key);
                    sumTime += time == null ? 0 : time;
                }
            }
            oiContestRankVo.setTotalTime(sumTime);
            oiContestRankVo.setTimeInfo(pidMapTime);
        }

        // ???????????????????????????,??????????????????????????????
        List<OIContestRankVO> orderResultList = result.stream()
                .sorted(Comparator.comparing(OIContestRankVO::getTotalScore, Comparator.reverseOrder())
                        .thenComparing(OIContestRankVO::getTotalTime, Comparator.naturalOrder()))
                .collect(Collectors.toList());
        return orderResultList;
    }


    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     * @param gid
     * @return
     */
    public List<String> getSuperAdminUidList(Long gid) {

        // ???????????????????????????
        List<String> superAdminUidList = userInfoEntityService.getSuperAdminUidList();

        if (gid != null) {
            QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
            groupMemberQueryWrapper.eq("gid", gid).eq("auth", 5);

            // ?????????????????????????????????????????????
            List<GroupMember> groupRootList = groupMemberEntityService.list(groupMemberQueryWrapper);

            for (GroupMember groupMember : groupRootList) {
                superAdminUidList.add(groupMember.getUid());
            }
        }
        return superAdminUidList;
    }

    /**
     * ????????????????????????
     * @param minSealRankTime
     * @param maxSealRankTime
     * @param time
     * @return
     */
    private boolean isInSealTimeSubmission(Long minSealRankTime, Long maxSealRankTime, Long time) {
        return time >= minSealRankTime && time <= maxSealRankTime;
    }

    private HashMap<String, Boolean> starAccountToMap(String starAccountStr) {
        if (StringUtils.isEmpty(starAccountStr)) {
            return new HashMap<>();
        }
        JSONObject jsonObject = JSONUtil.parseObj(starAccountStr);
        List<String> list = jsonObject.get("star_account", List.class);
        HashMap<String, Boolean> res = new HashMap<>();
        for (String str : list) {
            if (!StringUtils.isEmpty(str)) {
                res.put(str, true);
            }
        }
        return res;
    }

    private Queue<ContestAwardConfigVO> getContestAwardConfigList(String awardConfig, Integer awardType, Integer totalUser) {
        if (StringUtils.isEmpty(awardConfig)) {
            return new LinkedList<>();
        }
        JSONObject jsonObject = JSONUtil.parseObj(awardConfig);
        List<JSONObject> list = jsonObject.get("config", List.class);

        Queue<ContestAwardConfigVO> queue = new LinkedList<>();

        if (awardType == 1) {
            // ???????????????????????????
            for (JSONObject object : list) {
                ContestAwardConfigVO configVo = JSONUtil.toBean(object, ContestAwardConfigVO.class);
                if (configVo.getNum() != null && configVo.getNum() > 0) {
                    int num = (int) (configVo.getNum() * 0.01 * totalUser);
                    if (num > 0) {
                        configVo.setNum(num);
                        queue.offer(configVo);
                    }
                }
            }
        } else {
            for (JSONObject object : list) {
                ContestAwardConfigVO configVo = JSONUtil.toBean(object, ContestAwardConfigVO.class);
                if (configVo.getNum() != null && configVo.getNum() > 0) {
                    queue.offer(configVo);
                }
            }
        }
        return queue;
    }
}