package cn.edu.hqu.databackup.manager.group.training;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusForbiddenException;
import cn.edu.hqu.databackup.common.exception.StatusNotFoundException;
import cn.edu.hqu.databackup.dao.group.GroupEntityService;
import cn.edu.hqu.databackup.dao.group.GroupTrainingEntityService;
import cn.edu.hqu.databackup.dao.training.MappingTrainingCategoryEntityService;
import cn.edu.hqu.databackup.dao.training.TrainingCategoryEntityService;
import cn.edu.hqu.databackup.dao.training.TrainingEntityService;
import cn.edu.hqu.databackup.dao.training.TrainingRegisterEntityService;
import cn.edu.hqu.databackup.manager.admin.training.AdminTrainingRecordManager;
import cn.edu.hqu.databackup.pojo.dto.TrainingDTO;
import cn.edu.hqu.api.pojo.entity.group.Group;
import cn.edu.hqu.api.pojo.entity.training.MappingTrainingCategory;
import cn.edu.hqu.api.pojo.entity.training.Training;
import cn.edu.hqu.api.pojo.entity.training.TrainingCategory;
import cn.edu.hqu.api.pojo.entity.training.TrainingRegister;
import cn.edu.hqu.databackup.pojo.vo.TrainingVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;
import cn.edu.hqu.databackup.validator.GroupValidator;
import cn.edu.hqu.databackup.validator.TrainingValidator;

import java.util.Objects;

/**
 * @Author: egret
 */
@Component
public class GroupTrainingManager {

    @Autowired
    private GroupTrainingEntityService groupTrainingEntityService;

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private TrainingEntityService trainingEntityService;

    @Autowired
    private TrainingCategoryEntityService trainingCategoryEntityService;

    @Autowired
    private MappingTrainingCategoryEntityService mappingTrainingCategoryEntityService;

    @Autowired
    private TrainingRegisterEntityService trainingRegisterEntityService;

    @Autowired
    private AdminTrainingRecordManager adminTrainingRecordManager;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private TrainingValidator trainingValidator;

    public IPage<TrainingVO> getTrainingList(Integer limit, Integer currentPage, Long gid) throws StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        return groupTrainingEntityService.getTrainingList(limit, currentPage, gid);
    }

    public IPage<Training> getAdminTrainingList(Integer limit, Integer currentPage, Long gid) throws StatusNotFoundException, StatusForbiddenException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("???????????????????????????????????????????????????");
        }

        if (!isRoot && !groupValidator.isGroupAdmin(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;

        return groupTrainingEntityService.getAdminTrainingList(limit, currentPage, gid);
    }

    public TrainingDTO getTraining(Long tid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);

        if (training == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = training.getGid();

        if (gid == null){
            throw new StatusForbiddenException("???????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("?????????????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUsername().equals(training.getAuthor())
                && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        TrainingDTO trainingDto = new TrainingDTO();
        trainingDto.setTraining(training);

        QueryWrapper<MappingTrainingCategory> mappingTrainingCategoryQueryWrapper = new QueryWrapper<>();
        mappingTrainingCategoryQueryWrapper.eq("tid", tid);

        MappingTrainingCategory mappingTrainingCategory = mappingTrainingCategoryEntityService.getOne(mappingTrainingCategoryQueryWrapper);
        TrainingCategory trainingCategory = null;

        if (mappingTrainingCategory != null) {
            trainingCategory = trainingCategoryEntityService.getById(mappingTrainingCategory.getCid());
        }
        trainingDto.setTrainingCategory(trainingCategory);
        return trainingDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addTraining(TrainingDTO trainingDto) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {

        trainingValidator.validateTraining(trainingDto.getTraining());

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long gid = trainingDto.getTraining().getGid();
        if (gid == null){
            throw new StatusForbiddenException("????????????????????????????????????ID???????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("?????????????????????????????????????????????????????????");
        }

        if (!isRoot && !groupValidator.isGroupAdmin(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        trainingDto.getTraining().setIsGroup(true);

        Training training = trainingDto.getTraining();
        trainingEntityService.save(training);
        TrainingCategory trainingCategory = trainingDto.getTrainingCategory();

        if (trainingCategory.getGid() != null && !Objects.equals(trainingCategory.getGid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (trainingCategory.getId() == null) {
            try {
                trainingCategory.setGid(gid);
                trainingCategoryEntityService.save(trainingCategory);
            } catch (Exception ignored) {
                QueryWrapper<TrainingCategory> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("name", trainingCategory.getName());
                trainingCategory = trainingCategoryEntityService.getOne(queryWrapper, false);
            }
        }

        boolean isOk = mappingTrainingCategoryEntityService.save(new MappingTrainingCategory()
                .setTid(training.getId())
                .setCid(trainingCategory.getId()));
        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTraining(TrainingDTO trainingDto) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {

        trainingValidator.validateTraining(trainingDto.getTraining());

        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long tid = trainingDto.getTraining().getId();

        if (tid == null){
            throw new StatusForbiddenException("?????????????????????ID???????????????");
        }

        Training training = trainingEntityService.getById(tid);

        if (training == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = training.getGid();

        if (gid == null){
            throw new StatusForbiddenException("???????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("?????????????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUsername().equals(training.getAuthor()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        trainingDto.getTraining().setIsGroup(training.getIsGroup());

        trainingEntityService.updateById(trainingDto.getTraining());

        if (trainingDto.getTraining().getAuth().equals(Constants.Training.AUTH_PRIVATE.getValue())) {
            if (!Objects.equals(training.getPrivatePwd(), trainingDto.getTraining().getPrivatePwd())) {
                UpdateWrapper<TrainingRegister> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("tid", tid);
                trainingRegisterEntityService.remove(updateWrapper);
            }
        }

        TrainingCategory trainingCategory = trainingDto.getTrainingCategory();

        if (trainingCategory.getGid() != null && trainingCategory.getGid().longValue() != gid) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (trainingCategory.getId() == null) {
            try {
                trainingCategory.setGid(gid);
                trainingCategoryEntityService.save(trainingCategory);
            } catch (Exception ignored) {
                QueryWrapper<TrainingCategory> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("name", trainingCategory.getName());
                trainingCategory = trainingCategoryEntityService.getOne(queryWrapper, false);
            }
        }

        MappingTrainingCategory mappingTrainingCategory = mappingTrainingCategoryEntityService
                .getOne(new QueryWrapper<MappingTrainingCategory>().eq("tid", training.getId()),
                        false);

        if (mappingTrainingCategory == null) {
            mappingTrainingCategoryEntityService.save(new MappingTrainingCategory()
                    .setTid(training.getId()).setCid(trainingCategory.getId()));
            adminTrainingRecordManager.checkSyncRecord(trainingDto.getTraining());
        } else {
            if (!mappingTrainingCategory.getCid().equals(trainingCategory.getId())) {
                UpdateWrapper<MappingTrainingCategory> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("tid", training.getId()).set("cid", trainingCategory.getId());
                boolean isOk = mappingTrainingCategoryEntityService.update(null, updateWrapper);
                if (isOk) {
                    adminTrainingRecordManager.checkSyncRecord(trainingDto.getTraining());
                } else {
                    throw new StatusFailException("????????????");
                }
            }
        }
    }

    public void deleteTraining(Long tid) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);

        if (training == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = training.getGid();

        if (gid == null){
            throw new StatusForbiddenException("???????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("?????????????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUsername().equals(training.getAuthor()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        boolean isOk = trainingEntityService.removeById(tid);
        if (!isOk) {
            throw new StatusFailException("???????????????");
        }
    }

    public void changeTrainingStatus(Long tid, Boolean status) throws StatusForbiddenException, StatusNotFoundException, StatusFailException {
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Training training = trainingEntityService.getById(tid);

        if (training == null) {
            throw new StatusNotFoundException("?????????????????????");
        }

        Long gid = training.getGid();

        if (gid == null){
            throw new StatusForbiddenException("???????????????????????????????????????????????????");
        }

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("?????????????????????????????????????????????????????????");
        }

        if (!userRolesVo.getUsername().equals(training.getAuthor()) && !isRoot
                && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        UpdateWrapper<Training> trainingUpdateWrapper = new UpdateWrapper<>();
        trainingUpdateWrapper.eq("id", tid).set("status", status);

        boolean isOk = trainingEntityService.update(trainingUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("????????????");
        }
    }
}
