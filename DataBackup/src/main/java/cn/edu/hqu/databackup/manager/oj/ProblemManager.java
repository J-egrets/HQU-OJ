package cn.edu.hqu.databackup.manager.oj;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import cn.edu.hqu.databackup.annotation.HOJAccessEnum;
import cn.edu.hqu.databackup.common.exception.StatusAccessDeniedException;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.dao.contest.ContestEntityService;
import cn.edu.hqu.databackup.dao.judge.JudgeEntityService;
import cn.edu.hqu.databackup.dao.problem.*;
import cn.edu.hqu.databackup.exception.AccessException;
import cn.edu.hqu.databackup.pojo.dto.LastAcceptedCodeVO;
import cn.edu.hqu.databackup.pojo.dto.PidListDTO;
import cn.edu.hqu.api.pojo.entity.contest.Contest;
import cn.edu.hqu.api.pojo.entity.judge.Judge;
import cn.edu.hqu.api.pojo.entity.problem.*;
import cn.edu.hqu.databackup.pojo.vo.*;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.validator.AccessValidator;
import cn.edu.hqu.databackup.validator.ContestValidator;
import cn.edu.hqu.databackup.validator.GroupValidator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: egret
 */
@Component
public class ProblemManager {
    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private ProblemTagEntityService problemTagEntityService;

    @Autowired
    private JudgeEntityService judgeEntityService;

    @Autowired
    private TagEntityService tagEntityService;

    @Autowired
    private LanguageEntityService languageEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private ProblemLanguageEntityService problemLanguageEntityService;

    @Autowired
    private CodeTemplateEntityService codeTemplateEntityService;

    @Autowired
    private ContestValidator contestValidator;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private AccessValidator accessValidator;

    @Autowired
    private TrainingManager trainingManager;

    @Autowired
    private ContestManager contestManager;

    /**
     * @MethodName getProblemList
     * @Params * @param null
     * @Description ????????????????????????
     */
    public Page<ProblemVO> getProblemList(Integer limit, Integer currentPage,
                                          String keyword, List<Long> tagId, Integer difficulty, String oj) {
        // ????????????????????????????????????????????????
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        // ????????????????????????
        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
        }
        if (oj != null && !Constants.RemoteOJ.isRemoteOJ(oj)) {
            oj = "Mine";
        }
        return problemEntityService.getProblemList(limit, currentPage, null, keyword,
                difficulty, tagId, oj);
    }

    /**
     * @MethodName getRandomProblem
     * @Description ????????????????????????
     */
    public RandomProblemVO getRandomProblem() throws StatusFailException {
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        // ?????????????????????
        queryWrapper.select("problem_id").eq("auth", 1)
                .eq("is_group", false);
        List<Problem> list = problemEntityService.list(queryWrapper);
        if (list.size() == 0) {
            throw new StatusFailException("??????????????????????????????????????????????????????");
        }
        Random random = new Random();
        int index = random.nextInt(list.size());
        RandomProblemVO randomProblemVo = new RandomProblemVO();
        randomProblemVo.setProblemId(list.get(index).getProblemId());
        return randomProblemVo;
    }

    /**
     * @MethodName getUserProblemStatus
     * @Description ???????????????????????????????????????????????????????????????
     */
    public HashMap<Long, Object> getUserProblemStatus(PidListDTO pidListDto) throws StatusNotFoundException {

        // ?????????????????????token?????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        HashMap<Long, Object> result = new HashMap<>();
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        QueryWrapper<Judge> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct pid,status,submit_time,score")
                .in("pid", pidListDto.getPidList())
                .eq("uid", userRolesVo.getUid())
                .orderByDesc("submit_time");

        if (pidListDto.getIsContestProblemList()) {
            // ??????????????????????????????????????????cid
            queryWrapper.eq("cid", pidListDto.getCid());
        } else {
            queryWrapper.eq("cid", 0);
            if (pidListDto.getGid() != null) {
                queryWrapper.eq("gid", pidListDto.getGid());
            } else {
                queryWrapper.isNull("gid");
            }
        }
        List<Judge> judges = judgeEntityService.list(queryWrapper);

        boolean isACMContest = true;
        Contest contest = null;
        if (pidListDto.getIsContestProblemList()) {
            contest = contestEntityService.getById(pidListDto.getCid());
            if (contest == null) {
                throw new StatusNotFoundException("??????????????????????????????");
            }
            isACMContest = contest.getType().intValue() == Constants.Contest.TYPE_ACM.getCode();
        }
        boolean isSealRank = false;
        if (!isACMContest && CollectionUtil.isNotEmpty(judges)) {
            isSealRank = contestValidator.isSealRank(userRolesVo.getUid(), contest, false,
                    SecurityUtils.getSubject().hasRole("root"));
        }
        for (Judge judge : judges) {
            // ????????????????????????????????????
            HashMap<String, Object> temp = new HashMap<>();
            if (pidListDto.getIsContestProblemList()) {
                if (!isACMContest) {
                    if (!result.containsKey(judge.getPid())) {
                        // IO?????????????????????????????????????????????????????????????????????
                        // ?????????????????????????????????????????????,OI????????????????????????????????????????????????
                        // ??????????????????????????????,????????????????????????????????????????????????
                        if (isSealRank) {
                            temp.put("status", Constants.Judge.STATUS_SUBMITTED_UNKNOWN_RESULT.getStatus());
                            temp.put("score", null);
                        } else {
                            temp.put("status", judge.getStatus());
                            temp.put("score", judge.getScore());
                        }
                        result.put(judge.getPid(), temp);
                    }
                } else {
                    if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
                        // ??????????????????????????????????????????????????????????????????????????????????????????0???
                        temp.put("status", Constants.Judge.STATUS_ACCEPTED.getStatus());
                        temp.put("score", null);
                        result.put(judge.getPid(), temp);
                    } else if (!result.containsKey(judge.getPid())) {
                        // ???????????????????????????????????????????????????
                        temp.put("status", judge.getStatus());
                        temp.put("score", null);
                        result.put(judge.getPid(), temp);
                    }
                }

            } else { // ??????????????????
                if (judge.getStatus().intValue() == Constants.Judge.STATUS_ACCEPTED.getStatus()) {
                    // ???????????????????????????????????????????????????0???
                    temp.put("status", Constants.Judge.STATUS_ACCEPTED.getStatus());
                    result.put(judge.getPid(), temp);
                } else if (!result.containsKey(judge.getPid())) {
                    // ???????????????????????????????????????????????????
                    temp.put("status", judge.getStatus());
                    result.put(judge.getPid(), temp);
                }
            }
        }

        // ??????????????????????????????????????????????????????????????????-10
        for (Long pid : pidListDto.getPidList()) {
            // ????????????????????????????????????
            if (pidListDto.getIsContestProblemList()) {
                if (!result.containsKey(pid)) {
                    HashMap<String, Object> temp = new HashMap<>();
                    temp.put("score", null);
                    temp.put("status", Constants.Judge.STATUS_NOT_SUBMITTED.getStatus());
                    result.put(pid, temp);
                }
            } else {
                if (!result.containsKey(pid)) {
                    HashMap<String, Object> temp = new HashMap<>();
                    temp.put("status", Constants.Judge.STATUS_NOT_SUBMITTED.getStatus());
                    result.put(pid, temp);
                }
            }
        }
        return result;

    }

    /**
     * @MethodName getProblemInfo
     * @Description ?????????????????????????????????????????????????????????????????????????????????????????????????????? ?????????auth???1???
     */
    public ProblemInfoVO getProblemInfo(String problemId, Long gid) throws StatusNotFoundException, StatusForbiddenException {
        QueryWrapper<Problem> wrapper = new QueryWrapper<Problem>().eq("problem_id", problemId);
        //?????????????????????????????????????????????????????????????????????
        Problem problem = problemEntityService.getOne(wrapper, false);
        if (problem == null) {
            throw new StatusNotFoundException("?????????????????????????????????");
        }
        if (problem.getAuth() != 1) {
            throw new StatusForbiddenException("????????????????????????????????????????????????????????????");
        }

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        if (problem.getIsGroup() && !isRoot) {
            if (gid == null){
                throw new StatusForbiddenException("????????????????????????????????????????????????????????????????????????");
            }
            if(!groupValidator.isGroupMember(userRolesVo.getUid(), problem.getGid())) {
                throw new StatusForbiddenException("??????????????????????????????????????????????????????????????????????????????");
            }
        }

        QueryWrapper<ProblemTag> problemTagQueryWrapper = new QueryWrapper<>();
        problemTagQueryWrapper.eq("pid", problem.getId());

        // ??????????????????????????????id
        List<Long> tidList = new LinkedList<>();
        problemTagEntityService.list(problemTagQueryWrapper).forEach(problemTag -> {
            tidList.add(problemTag.getTid());
        });
        List<Tag> tags = new ArrayList<>();
        if (tidList.size() > 0) {
            tags = (List<Tag>) tagEntityService.listByIds(tidList);
        }

        // ?????? languageId?????????name
        HashMap<Long, String> tmpMap = new HashMap<>();
        // ??????????????????????????????????????????
        List<String> languagesStr = new LinkedList<>();
        QueryWrapper<ProblemLanguage> problemLanguageQueryWrapper = new QueryWrapper<>();
        problemLanguageQueryWrapper.eq("pid", problem.getId()).select("lid");
        List<Long> lidList = problemLanguageEntityService.list(problemLanguageQueryWrapper)
                .stream().map(ProblemLanguage::getLid).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(lidList)) {
            Collection<Language> languages = languageEntityService.listByIds(lidList);
            languages = languages.stream().sorted(Comparator.comparing(Language::getSeq, Comparator.reverseOrder())
                            .thenComparing(Language::getId))
                    .collect(Collectors.toList());
            languages.forEach(language -> {
                languagesStr.add(language.getName());
                tmpMap.put(language.getId(), language.getName());
            });
        }
        // ???????????????????????????
        ProblemCountVO problemCount = judgeEntityService.getProblemCount(problem.getId(), gid);

        // ???????????????????????????
        QueryWrapper<CodeTemplate> codeTemplateQueryWrapper = new QueryWrapper<>();
        codeTemplateQueryWrapper.eq("pid", problem.getId()).eq("status", true);
        List<CodeTemplate> codeTemplates = codeTemplateEntityService.list(codeTemplateQueryWrapper);
        HashMap<String, String> LangNameAndCode = new HashMap<>();
        if (CollectionUtil.isNotEmpty(codeTemplates)) {
            for (CodeTemplate codeTemplate : codeTemplates) {
                LangNameAndCode.put(tmpMap.get(codeTemplate.getLid()), codeTemplate.getCode());
            }
        }
        // ????????????????????????
        problem.setJudgeExtraFile(null)
                .setSpjCode(null)
                .setSpjLanguage(null);

        // ??????????????????????????????Vo????????????????????????
        return new ProblemInfoVO(problem, tags, languagesStr, problemCount, LangNameAndCode);
    }

    public LastAcceptedCodeVO getUserLastAcceptedCode(Long pid, Long cid) {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        if (cid == null) {
            cid = 0L;
        }
        QueryWrapper<Judge> judgeQueryWrapper = new QueryWrapper<>();
        judgeQueryWrapper.select("submit_id", "cid", "code", "username", "submit_time", "language")
                .eq("uid", userRolesVo.getUid())
                .eq("pid", pid)
                .eq("cid", cid)
                .eq("status", 0)
                .orderByDesc("submit_id")
                .last("limit 1");
        List<Judge> judgeList = judgeEntityService.list(judgeQueryWrapper);
        LastAcceptedCodeVO lastAcceptedCodeVO = new LastAcceptedCodeVO();
        if (CollectionUtil.isNotEmpty(judgeList)) {
            Judge judge = judgeList.get(0);
            lastAcceptedCodeVO.setSubmitId(judge.getSubmitId());
            lastAcceptedCodeVO.setLanguage(judge.getLanguage());
            lastAcceptedCodeVO.setCode(buildCode(judge));
        } else {
            lastAcceptedCodeVO.setCode("");
        }
        return lastAcceptedCodeVO;
    }

    private String buildCode(Judge judge) {
        if (judge.getCid() == 0) {
            // ???????????????????????? ???????????????????????????????????????????????????????????????????????????????????????
            boolean isRoot = SecurityUtils.getSubject().hasRole("root"); // ????????????????????????
            boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");// ????????????????????????
            if (!isRoot && !isProblemAdmin) {
                try {
                    accessValidator.validateAccess(HOJAccessEnum.HIDE_NON_CONTEST_SUBMISSION_CODE);
                } catch (AccessException e) {
                    return "Because the super administrator has enabled " +
                            "the function of not viewing the submitted code outside the contest of master station, \n" +
                            "the code of this submission details has been hidden.";
                }
            }
        }
        if (!judge.getLanguage().toLowerCase().contains("py")) {
            return judge.getCode() + "\n\n" +
                    "/**\n" +
                    "* @runId: " + judge.getSubmitId() + "\n" +
                    "* @language: " + judge.getLanguage() + "\n" +
                    "* @author: " + judge.getUsername() + "\n" +
                    "* @submitTime: " + DateUtil.format(judge.getSubmitTime(), "yyyy-MM-dd HH:mm:ss") + "\n" +
                    "*/";
        } else {
            return judge.getCode() + "\n\n" +
                    "'''\n" +
                    "    @runId: " + judge.getSubmitId() + "\n" +
                    "    @language: " + judge.getLanguage() + "\n" +
                    "    @author: " + judge.getUsername() + "\n" +
                    "    @submitTime: " + DateUtil.format(judge.getSubmitTime(), "yyyy-MM-dd HH:mm:ss") + "\n" +
                    "'''";
        }
    }

    public List<ProblemFullScreenListVO> getFullScreenProblemList(Long tid, Long cid)
            throws StatusFailException, StatusForbiddenException, StatusAccessDeniedException {
        if (tid != null) {
            return trainingManager.getProblemFullScreenList(tid);
        } else if (cid != null && cid != 0) {
            return contestManager.getContestFullScreenProblemList(cid);
        } else {
            throw new StatusFailException("?????????????????????tid???cid????????????");
        }
    }
}