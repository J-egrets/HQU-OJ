package cn.edu.hqu.databackup.manager.file;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import cn.edu.hqu.databackup.common.exception.StatusFailException;
import cn.edu.hqu.databackup.common.exception.StatusSystemErrorException;
import cn.edu.hqu.databackup.common.result.ResultStatus;
import cn.edu.hqu.databackup.dao.problem.LanguageEntityService;
import cn.edu.hqu.databackup.dao.problem.ProblemCaseEntityService;
import cn.edu.hqu.databackup.dao.problem.ProblemEntityService;
import cn.edu.hqu.databackup.dao.problem.TagEntityService;
import cn.edu.hqu.databackup.exception.ProblemIDRepeatException;
import cn.edu.hqu.databackup.pojo.dto.ProblemDTO;
import cn.edu.hqu.api.pojo.entity.problem.*;
import cn.edu.hqu.databackup.pojo.vo.ImportProblemVO;
import cn.edu.hqu.databackup.shiro.AccountProfile;
import cn.edu.hqu.databackup.utils.Constants;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Author: egret
 */
@Component
@Slf4j(topic = "hoj")
public class ProblemFileManager {
    @Autowired
    private LanguageEntityService languageEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private ProblemCaseEntityService problemCaseEntityService;

    @Autowired
    private TagEntityService tagEntityService;

    /**
     * @param file
     * @MethodName importProblem
     * @Description zip?????????????????? ???????????????????????????
     * @Return
     */
    public void importProblem(MultipartFile file) throws StatusFailException, StatusSystemErrorException {

        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (!"zip".toUpperCase().contains(suffix.toUpperCase())) {
            throw new StatusFailException("?????????zip?????????????????????????????????");
        }

        String fileDirId = IdUtil.simpleUUID();
        String fileDir = Constants.File.TESTCASE_TMP_FOLDER.getPath() + File.separator + fileDirId;
        String filePath = fileDir + File.separator + file.getOriginalFilename();
        // ???????????????????????????
        FileUtil.mkdir(fileDir);
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            FileUtil.del(fileDir);
            throw new StatusSystemErrorException("?????????????????????????????????????????????");
        }

        // ????????????????????????????????????
        ZipUtil.unzip(filePath, fileDir);

        // ??????zip??????
        FileUtil.del(filePath);


        // ????????????????????????
        File testCaseFileList = new File(fileDir);
        File[] files = testCaseFileList.listFiles();
        if (files == null || files.length == 0) {
            FileUtil.del(fileDir);
            throw new StatusFailException("?????????????????????????????????????????????");
        }


        HashMap<String, File> problemInfo = new HashMap<>();
        HashMap<String, File> testcaseInfo = new HashMap<>();

        for (File tmp : files) {
            if (tmp.isFile()) {
                // ?????????????????????json??????
                if (!tmp.getName().endsWith("json")) {
                    FileUtil.del(fileDir);
                    throw new StatusFailException("????????????" + tmp.getName() + "?????????????????????????????????json?????????");
                }
                String tmpPreName = tmp.getName().substring(0, tmp.getName().lastIndexOf("."));
                problemInfo.put(tmpPreName, tmp);
            }
            if (tmp.isDirectory()) {
                testcaseInfo.put(tmp.getName(), tmp);
            }
        }

        // ??????json??????????????????
        HashMap<String, ImportProblemVO> problemVoMap = new HashMap<>();
        for (String key : problemInfo.keySet()) {
            // ??????????????????????????????????????????
            if (testcaseInfo.getOrDefault(key, null) == null) {
                FileUtil.del(fileDir);
                throw new StatusFailException("?????????????????????" + key + "??????????????????????????????????????????????????????????????????");
            }
            try {
                FileReader fileReader = new FileReader(problemInfo.get(key));
                ImportProblemVO importProblemVo = JSONUtil.toBean(fileReader.readString(), ImportProblemVO.class);
                problemVoMap.put(key, importProblemVo);
            } catch (Exception e) {
                FileUtil.del(fileDir);
                throw new StatusFailException("?????????????????????" + key + "?????????json??????????????????" + e.getLocalizedMessage());
            }
        }

        QueryWrapper<Language> languageQueryWrapper = new QueryWrapper<>();
        languageQueryWrapper.eq("oj", "ME");
        List<Language> languageList = languageEntityService.list(languageQueryWrapper);

        HashMap<String, Long> languageMap = new HashMap<>();
        for (Language language : languageList) {
            languageMap.put(language.getName(), language.getId());
        }

        // ???????????????????????????
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        List<ProblemDTO> problemDTOS = new LinkedList<>();
        List<Tag> tagList = tagEntityService.list(new QueryWrapper<Tag>().eq("oj", "ME"));
        HashMap<String, Tag> tagMap = new HashMap<>();
        for (Tag tag : tagList) {
            tagMap.put(tag.getName().toUpperCase(), tag);
        }
        for (String key : problemInfo.keySet()) {
            ImportProblemVO importProblemVo = problemVoMap.get(key);
            // ?????????????????????
            List<Language> languages = new LinkedList<>();
            for (String lang : importProblemVo.getLanguages()) {
                Long lid = languageMap.getOrDefault(lang, null);

                if (lid == null) {
                    throw new StatusFailException("?????????????????????" + key + "????????????????????????????????????????????????????????????????????????");
                }
                languages.add(new Language().setId(lid).setName(lang));
            }

            // ???????????????????????????
            List<CodeTemplate> codeTemplates = new LinkedList<>();
            for (Map<String, String> tmp : importProblemVo.getCodeTemplates()) {
                String language = tmp.getOrDefault("language", null);
                String code = tmp.getOrDefault("code", null);
                Long lid = languageMap.getOrDefault(language, null);
                if (language == null || code == null || lid == null) {
                    FileUtil.del(fileDir);
                    throw new StatusFailException("?????????????????????" + key + "??????????????????????????????????????????????????????????????????????????????");
                }
                codeTemplates.add(new CodeTemplate().setCode(code).setStatus(true).setLid(lid));
            }

            // ???????????????
            List<Tag> tags = new LinkedList<>();
            for (String tagStr : importProblemVo.getTags()) {
                Tag tag = tagMap.getOrDefault(tagStr.toUpperCase(), null);
                if (tag == null) {
                    tags.add(new Tag().setName(tagStr).setOj("ME"));
                } else {
                    tags.add(tag);
                }
            }

            Problem problem = BeanUtil.mapToBean(importProblemVo.getProblem(), Problem.class, true);
            if (problem.getAuthor() == null) {
                problem.setAuthor(userRolesVo.getUsername());
            }
            problem.setIsGroup(false);
            List<ProblemCase> problemCaseList = new LinkedList<>();
            for (Map<String, Object> tmp : importProblemVo.getSamples()) {
                problemCaseList.add(BeanUtil.mapToBean(tmp, ProblemCase.class, true));
            }

            // ????????????????????????????????????????????????
            if (importProblemVo.getUserExtraFile() != null) {
                JSONObject userExtraFileJson = JSONUtil.parseObj(importProblemVo.getUserExtraFile());
                problem.setUserExtraFile(userExtraFileJson.toString());
            }
            if (importProblemVo.getJudgeExtraFile() != null) {
                JSONObject judgeExtraFileJson = JSONUtil.parseObj(importProblemVo.getJudgeExtraFile());
                problem.setJudgeExtraFile(judgeExtraFileJson.toString());
            }


            ProblemDTO problemDto = new ProblemDTO();
            problemDto.setProblem(problem)
                    .setCodeTemplates(codeTemplates)
                    .setTags(tags)
                    .setLanguages(languages)
                    .setUploadTestcaseDir(fileDir + File.separator + key)
                    .setIsUploadTestCase(true)
                    .setSamples(problemCaseList);

            Constants.JudgeMode judgeMode = Constants.JudgeMode.getJudgeMode(importProblemVo.getJudgeMode());
            if (judgeMode == null) {
                problemDto.setJudgeMode(Constants.JudgeMode.DEFAULT.getMode());
            } else {
                problemDto.setJudgeMode(judgeMode.getMode());
            }
            problemDTOS.add(problemDto);
        }

        if (problemDTOS.size() == 0) {
            throw new StatusFailException("????????????????????????????????????????????????????????????????????????????????????");
        } else {
            HashSet<String> repeatProblemIDSet = new HashSet<>();
            HashSet<String> failedProblemIDSet = new HashSet<>();
            int failedCount = 0;
            for (ProblemDTO problemDto : problemDTOS) {
                try {
                    boolean isOk = problemEntityService.adminAddProblem(problemDto);
                    if (!isOk) {
                        failedCount++;
                    }
                } catch (ProblemIDRepeatException e) {
                    repeatProblemIDSet.add(problemDto.getProblem().getProblemId());
                    failedCount++;
                } catch (Exception e) {
                    log.error("", e);
                    failedProblemIDSet.add(problemDto.getProblem().getProblemId());
                    failedCount++;
                }
            }
            if (failedCount > 0) {
                int successCount = problemDTOS.size() - failedCount;
                String errMsg = "[????????????] ????????????" + successCount + ",  ????????????" + failedCount +
                        ",  ?????????????????????ID???" + repeatProblemIDSet;
                if (failedProblemIDSet.size() > 0) {
                    errMsg = errMsg + "<br/>?????????????????????ID???" + failedProblemIDSet;
                }
                throw new StatusFailException(errMsg);
            }
        }
    }


    /**
     * @param pidList
     * @param response
     * @MethodName exportProblem
     * @Description ?????????????????????????????????????????????zip ???????????????????????????
     * @Return
     */
    public void exportProblem(List<Long> pidList, HttpServletResponse response) {

        QueryWrapper<Language> languageQueryWrapper = new QueryWrapper<>();
        languageQueryWrapper.eq("oj", "ME");
        List<Language> languageList = languageEntityService.list(languageQueryWrapper);

        HashMap<Long, String> languageMap = new HashMap<>();
        for (Language language : languageList) {
            languageMap.put(language.getId(), language.getName());
        }

        List<Tag> tagList = tagEntityService.list();

        HashMap<Long, String> tagMap = new HashMap<>();
        for (Tag tag : tagList) {
            tagMap.put(tag.getId(), tag.getName());
        }

        String workDir = Constants.File.FILE_DOWNLOAD_TMP_FOLDER.getPath() + File.separator + IdUtil.simpleUUID();

        // ???????????????
        ExecutorService threadPool = new ThreadPoolExecutor(
                2, // ???????????????
                4, // ?????????????????????????????????????????????
                3,//?????????????????????????????????????????????????????????
                TimeUnit.SECONDS,// ????????????????????????
                new LinkedBlockingDeque<>(200), //????????????????????????????????????
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());//?????????????????????????????????????????????????????????????????????

        List<FutureTask<Void>> futureTasks = new ArrayList<>();

        for (Long pid : pidList) {

            futureTasks.add(new FutureTask<>(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    String testcaseWorkDir = Constants.File.TESTCASE_BASE_FOLDER.getPath() + File.separator + "problem_" + pid;
                    File file = new File(testcaseWorkDir);

                    List<HashMap<String, Object>> problemCases = new LinkedList<>();
                    if (!file.exists() || file.listFiles() == null) { // ???????????? ????????????????????????
                        QueryWrapper<ProblemCase> problemCaseQueryWrapper = new QueryWrapper<>();
                        problemCaseQueryWrapper.eq("pid", pid);
                        List<ProblemCase> problemCaseList = problemCaseEntityService.list(problemCaseQueryWrapper);
                        FileUtil.mkdir(testcaseWorkDir);
                        // ????????????
                        for (int i = 0; i < problemCaseList.size(); i++) {
                            String filePreName = testcaseWorkDir + File.separator + (i + 1);
                            String inputName = filePreName + ".in";
                            String outputName = filePreName + ".out";
                            FileWriter infileWriter = new FileWriter(inputName);
                            infileWriter.write(problemCaseList.get(i).getInput());
                            FileWriter outfileWriter = new FileWriter(outputName);
                            outfileWriter.write(problemCaseList.get(i).getOutput());

                            ProblemCase problemCase = problemCaseList.get(i).setPid(null)
                                    .setInput(inputName)
                                    .setOutput(outputName)
                                    .setGmtCreate(null)
                                    .setStatus(null)
                                    .setId(null)
                                    .setGmtModified(null);
                            HashMap<String, Object> problemCaseMap = new HashMap<>();
                            BeanUtil.beanToMap(problemCase, problemCaseMap, false, true);
                            problemCases.add(problemCaseMap);
                        }
                        FileUtil.copy(testcaseWorkDir, workDir, true);

                    } else {
                        String infoPath = testcaseWorkDir + File.separator + "info";
                        if (FileUtil.exist(infoPath)) {
                            FileReader reader = new FileReader(infoPath);
                            JSONObject jsonObject = JSONUtil.parseObj(reader.readString());
                            JSONArray testCases = jsonObject.getJSONArray("testCases");
                            for (int i = 0; i < testCases.size(); i++) {
                                JSONObject jsonObject1 = testCases.get(i, JSONObject.class);
                                HashMap<String, Object> problemCaseMap = new HashMap<>();
                                problemCaseMap.put("input", jsonObject1.getStr("inputName"));
                                problemCaseMap.put("output", jsonObject1.getStr("outputName"));
                                Integer score = jsonObject1.getInt("score");
                                Integer groupNum = jsonObject1.getInt("groupNum");
                                if (score != null && score > 0) {
                                    problemCaseMap.put("score", score);
                                }
                                if (groupNum != null) {
                                    problemCaseMap.put("groupNum", groupNum);
                                }
                                problemCases.add(problemCaseMap);
                            }
                        }
                        FileUtil.copy(testcaseWorkDir, workDir, true);
                    }
                    ImportProblemVO importProblemVo = problemEntityService.buildExportProblem(pid, problemCases, languageMap, tagMap);
                    String content = JSONUtil.toJsonStr(importProblemVo);
                    FileWriter fileWriter = new FileWriter(workDir + File.separator + "problem_" + pid + ".json");
                    fileWriter.write(content);
                    return null;
                }
            }));

        }
        // ??????????????????????????????
        for (FutureTask<Void> futureTask : futureTasks) {
            threadPool.submit(futureTask);
        }
        // ?????????????????????????????????????????????????????????????????????
        if (!threadPool.isShutdown()) {
            threadPool.shutdown();
        }
        // ???????????????, ?????????????????????
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            log.error("???????????????--------------->", e);
        }

        String fileName = "problem_export_" + System.currentTimeMillis() + ".zip";
        // ????????????????????????????????????zip
        ZipUtil.zip(workDir, Constants.File.FILE_DOWNLOAD_TMP_FOLDER.getPath() + File.separator + fileName);
        // ???zip??????io??????????????????
        FileReader fileReader = new FileReader(Constants.File.FILE_DOWNLOAD_TMP_FOLDER.getPath() + File.separator + fileName);
        BufferedInputStream bins = new BufferedInputStream(fileReader.getInputStream());//?????????????????????
        OutputStream outs = null;//??????????????????IO???
        BufferedOutputStream bouts = null;
        try {
            outs = response.getOutputStream();
            bouts = new BufferedOutputStream(outs);
            response.setContentType("application/x-download");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            int bytesRead = 0;
            byte[] buffer = new byte[1024 * 10];
            //??????????????????????????????
            while ((bytesRead = bins.read(buffer, 0, 1024 * 10)) != -1) {
                bouts.write(buffer, 0, bytesRead);
            }
            bouts.flush();
        } catch (IOException e) {
            log.error("???????????????????????????????????????------------>", e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, Object> map = new HashMap<>();
            map.put("status", ResultStatus.SYSTEM_ERROR);
            map.put("msg", "?????????????????????????????????????????????");
            map.put("data", null);
            try {
                response.getWriter().println(JSONUtil.toJsonStr(map));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                bins.close();
                if (outs != null) {
                    outs.close();
                }
                if (bouts != null) {
                    bouts.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // ??????????????????
            FileUtil.del(workDir);
            FileUtil.del(Constants.File.FILE_DOWNLOAD_TMP_FOLDER.getPath() + File.separator + fileName);
        }
    }

}