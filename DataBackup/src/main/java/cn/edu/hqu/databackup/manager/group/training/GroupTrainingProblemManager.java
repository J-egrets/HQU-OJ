package cn.edu.hqu.databackup.manager.group.training;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.dao.group.GroupEntityService;
import cn.edu.hqu.databackup.dao.problem.ProblemEntityService;
import cn.edu.hqu.databackup.dao.training.TrainingEntityService;
import cn.edu.hqu.databackup.dao.training.TrainingProblemEntityService;
import cn.edu.hqu.databackup.manager.admin.training.AdminTrainingProblemManager;
import cn.edu.hqu.databackup.manager.admin.training.AdminTrainingRecordManager;
import cn.edu.hqu.databackup.pojo.dto.TrainingProblemDTO;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.api.pojo.entity.problem.Problem;
import cn.edu.hqu.api.pojo.entity.training.Training;
import cn.edu.hqu.api.pojo.entity.training.TrainingProblem;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.validator.GroupValidator;

import java.util.Date;
import java.util.HashMap;

/**
 * @Author: egret
 */
@Component
public class GroupTrainingProblemManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private TrainingEntityService trainingEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private AdminTrainingProblemManager adminTrainingProblemManager;

    @Autowired
    private TrainingProblemEntityService trainingProblemEntityService;

    @Autowired
    private AdminTrainingRecordManager adminTrainingRecordManager;

    @Autowired
    private GroupValidator groupValidator;

    public HashMap<String, Object> getTrainingProblemList(Integer limit, Integer currentPage, String keyword, Boolean queryExisted, Long tid) throws StatusNotFoundException, StatusForbiddenException {

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);

        if (training == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = training.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUsername().equals(training.getAuthor()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        return adminTrainingProblemManager.getProblemList(limit, currentPage, keyword, queryExisted, tid);
    }

    public void updateTrainingProblem(TrainingProblem trainingProblem) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(trainingProblem.getTid());

        if (training == null) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long gid = training.getGid();

        if (gid == null){
            throw new StatusForbiddenException("?????????????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUsername().equals(training.getAuthor()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = trainingProblemEntityService.updateById(trainingProblem);
        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }

    public void deleteTrainingProblem(Long pid, Long tid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);

        if (training == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = training.getGid();

        if (gid == null){
            throw new StatusForbiddenException("?????????????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUsername().equals(training.getAuthor()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<TrainingProblem> trainingProblemQueryWrapper = new QueryWrapper<>();
        trainingProblemQueryWrapper.eq("tid", tid).eq("pid", pid);

        boolean isOk = trainingProblemEntityService.remove(trainingProblemQueryWrapper);
        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProblemFromPublic(TrainingProblemDTO trainingProblemDto) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long pid = trainingProblemDto.getPid();

        Problem problem = problemEntityService.getById(pid);

        if (problem == null || problem.getAuth() != 1 || problem.getIsGroup()) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        Long tid = trainingProblemDto.getTid();

        Training training = trainingEntityService.getById(tid);

        if (training == null) {
            throw new StatusNotFoundException("??????????????????????????????????????????");
        }

        Long gid = training.getGid();

        if (gid == null){
            throw new StatusForbiddenException("?????????????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUsername().equals(training.getAuthor()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        String displayId = trainingProblemDto.getDisplayId();

        QueryWrapper<TrainingProblem> trainingProblemQueryWrapper = new QueryWrapper<>();
        trainingProblemQueryWrapper.eq("tid", tid)
                .and(wrapper -> wrapper.eq("pid", pid)
                        .or()
                        .eq("display_id", displayId));
        TrainingProblem trainingProblem = trainingProblemEntityService.getOne(trainingProblemQueryWrapper);
        if (trainingProblem != null) {
            throw new StatusFailException("????????????????????????????????????????????????????????????ID????????????");
        }

        TrainingProblem newTProblem = new TrainingProblem();

        boolean isOk = trainingProblemEntityService.saveOrUpdate(newTProblem
                .setTid(tid).setPid(pid).setDisplayId(displayId));
        if (isOk) { // ????????????

            // ??????????????????????????????
            UpdateWrapper<Training> trainingUpdateWrapper = new UpdateWrapper<>();
            trainingUpdateWrapper.set("gmt_modified", new Date())
                    .eq("id", tid);
            trainingEntityService.update(trainingUpdateWrapper);

            adminTrainingRecordManager.syncAlreadyRegisterUserRecord(tid, pid, newTProblem.getId());
        } else {
            throw new StatusFailException("???????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProblemFromGroup(String problemId, Long tid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);

        if (training == null) {
            throw new StatusNotFoundException("??????????????????????????????????????????");
        }

        Long gid = training.getGid();

        if (gid == null){
            throw new StatusForbiddenException("?????????????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUsername().equals(training.getAuthor()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq("problem_id", problemId).eq("gid", gid);

        Problem problem = problemEntityService.getOne(problemQueryWrapper);

        if (problem == null) {
            throw new StatusNotFoundException("?????????????????????????????????????????????????????????");
        }

        QueryWrapper<TrainingProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tid", tid)
                .and(wrapper -> wrapper.eq("pid", problem.getId())
                        .or()
                        .eq("display_id", problem.getProblemId()));

        TrainingProblem trainingProblem = trainingProblemEntityService.getOne(queryWrapper);

        if (trainingProblem != null) {
            throw new StatusFailException("????????????????????????????????????????????????????????????ID????????????");
        }

        TrainingProblem newTProblem = new TrainingProblem();
        boolean isOk = trainingProblemEntityService.save(newTProblem
                .setTid(tid).setPid(problem.getId()).setDisplayId(problem.getProblemId()));
        if (isOk) {
            UpdateWrapper<Training> trainingUpdateWrapper = new UpdateWrapper<>();
            trainingUpdateWrapper.set("gmt_modified", new Date())
                    .eq("id", tid);
            trainingEntityService.update(trainingUpdateWrapper);
            adminTrainingRecordManager.syncAlreadyRegisterUserRecord(tid, problem.getId(), newTProblem.getId());
        } else {
            throw new StatusFailException("???????????????");
        }
    }
}
