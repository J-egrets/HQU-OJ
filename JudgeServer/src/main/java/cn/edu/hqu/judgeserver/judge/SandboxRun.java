package cn.edu.hqu.judgeserver.judge;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import cn.edu.hqu.judgeserver.common.exception.SystemError;
import cn.edu.hqu.judgeserver.util.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author egret
 */

/**
 * args: string[]; // command line argument
 * env?: string[]; // environment
 * <p>
 * // specifies file input / pipe collector for program file descriptors
 * files?: (LocalFile | MemoryFile | PreparedFile | Pipe | null)[];
 * tty?: boolean; // enables tty on the input and output pipes (should have just one input & one output)
 * // Notice: must have TERM environment variables (e.g. TERM=xterm)
 * <p>
 * // limitations
 * cpuLimit?: number;     // ns
 * realCpuLimit?: number; // deprecated: use clock limit instead (still working)
 * clockLimit?: number;   // ns
 * memoryLimit?: number;  // byte
 * stackLimit?: number;   // byte (N/A on windows, macOS cannot set over 32M)
 * procLimit?: number;
 * <p>
 * // copy the correspond file to the container dst path
 * copyIn?: {[dst:string]:LocalFile | MemoryFile | PreparedFile};
 * <p>
 * // copy out specifies files need to be copied out from the container after execution
 * copyOut?: string[];
 * // similar to copyOut but stores file in executor service and returns fileId, later download through /file/:fileId
 * copyOutCached?: string[];
 * // specifies the directory to dump container /w content
 * copyOutDir: string
 * // specifies the max file size to copy out
 * copyOutMax: number; // byte
 */

@Slf4j(topic = "hoj")
public class SandboxRun {

    private static final RestTemplate restTemplate;

    // ????????????
    private static final SandboxRun instance = new SandboxRun();

    private static final String SANDBOX_BASE_URL = "http://localhost:5050";

    public static final HashMap<String, Integer> RESULT_MAP_STATUS = new HashMap<>();

    private static final int maxProcessNumber = 128;

    private static final int TIME_LIMIT_MS = 16000;

    private static final int MEMORY_LIMIT_MB = 512;

    private static final int STACK_LIMIT_MB = 128;

    private static final int STDIO_SIZE_MB = 32;

    private SandboxRun() {

    }

    static {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(20000);
        requestFactory.setReadTimeout(180000);
        restTemplate = new RestTemplate(requestFactory);
    }

    static {
        RESULT_MAP_STATUS.put("Time Limit Exceeded", Constants.Judge.STATUS_TIME_LIMIT_EXCEEDED.getStatus());
        RESULT_MAP_STATUS.put("Memory Limit Exceeded", Constants.Judge.STATUS_MEMORY_LIMIT_EXCEEDED.getStatus());
        RESULT_MAP_STATUS.put("Output Limit Exceeded", Constants.Judge.STATUS_RUNTIME_ERROR.getStatus());
        RESULT_MAP_STATUS.put("Accepted", Constants.Judge.STATUS_ACCEPTED.getStatus());
        RESULT_MAP_STATUS.put("Nonzero Exit Status", Constants.Judge.STATUS_RUNTIME_ERROR.getStatus());
        RESULT_MAP_STATUS.put("Internal Error", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
        RESULT_MAP_STATUS.put("File Error", Constants.Judge.STATUS_SYSTEM_ERROR.getStatus());
        RESULT_MAP_STATUS.put("Signalled", Constants.Judge.STATUS_RUNTIME_ERROR.getStatus());
    }

    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public static String getSandboxBaseUrl() {
        return SANDBOX_BASE_URL;
    }

    public static final List<String> signals = Arrays.asList(
            "", // 0
            "Hangup", // 1
            "Interrupt", // 2
            "Quit", // 3
            "Illegal instruction", // 4
            "Trace/breakpoint trap", // 5
            "Aborted", // 6
            "Bus error", // 7
            "Floating point exception", // 8
            "Killed", // 9
            "User defined signal 1", // 10
            "Segmentation fault", // 11
            "User defined signal 2", // 12
            "Broken pipe", // 13
            "Alarm clock", // 14
            "Terminated", // 15
            "Stack fault", // 16
            "Child exited", // 17
            "Continued", // 18
            "Stopped (signal)", // 19
            "Stopped", // 20
            "Stopped (tty input)", // 21
            "Stopped (tty output)", // 22
            "Urgent I/O condition", // 23
            "CPU time limit exceeded", // 24
            "File size limit exceeded", // 25
            "Virtual timer expired", // 26
            "Profiling timer expired", // 27
            "Window changed", // 28
            "I/O possible", // 29
            "Power failure", // 30
            "Bad system call" // 31
    );

    public JSONArray run(String uri, JSONObject param) throws SystemError {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JSONUtil.toJsonStr(param), headers);
        ResponseEntity<String> postForEntity;
        try {
            postForEntity = restTemplate.postForEntity(SANDBOX_BASE_URL + uri, request, String.class);
            return JSONUtil.parseArray(postForEntity.getBody());
        } catch (RestClientResponseException ex) {
            if (ex.getRawStatusCode() != 200) {
                throw new SystemError("Cannot connect to sandbox service.", null, ex.getResponseBodyAsString());
            }
        } catch (Exception e) {
            throw new SystemError("Call SandBox Error.", null, e.getMessage());
        }
        return null;
    }

    public static void delFile(String fileId) {

        try {
            restTemplate.delete(SANDBOX_BASE_URL + "/file/{0}", fileId);
        } catch (RestClientResponseException ex) {
            if (ex.getRawStatusCode() != 200) {
                log.error("???????????????????????????????????????????????????????????????----------------->{}", ex.getResponseBodyAsString());
            }
        }

    }

    /**
     * "files": [{
     * "content": ""
     * }, {
     * "name": "stdout",
     * "max": 1024 * 1024 * 32
     * }, {
     * "name": "stderr",
     * "max": 1024 * 1024 * 32
     * }]
     */
    private static final JSONArray COMPILE_FILES = new JSONArray();

    static {
        JSONObject content = new JSONObject();
        content.set("content", "");

        JSONObject stdout = new JSONObject();
        stdout.set("name", "stdout");
        stdout.set("max", 1024 * 1024 * STDIO_SIZE_MB);

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        COMPILE_FILES.put(content);
        COMPILE_FILES.put(stdout);
        COMPILE_FILES.put(stderr);
    }

    /**
     * @param maxCpuTime        ???????????????cpu?????? ms
     * @param maxRealTime       ??????????????????????????? ms
     * @param maxMemory         ????????????????????? b
     * @param maxStack          ???????????????????????? b
     * @param srcName           ????????????????????????
     * @param exeName           ???????????????exe????????????
     * @param args              ?????????cmd??????
     * @param envs              ?????????????????????
     * @param code              ??????????????????
     * @param extraFiles        ??????????????????????????? key:????????????value:????????????
     * @param needCopyOutCached ?????????????????????????????????????????????????????????????????????id
     * @param needCopyOutExe    ??????????????????????????????????????????exe??????
     * @param copyOutDir        ??????????????????????????????exe?????????????????????
     * @MethodName compile
     * @Description ????????????
     * @Return
     */
    public static JSONArray compile(Long maxCpuTime,
                                    Long maxRealTime,
                                    Long maxMemory,
                                    Long maxStack,
                                    String srcName,
                                    String exeName,
                                    List<String> args,
                                    List<String> envs,
                                    String code,
                                    HashMap<String, String> extraFiles,
                                    Boolean needCopyOutCached,
                                    Boolean needCopyOutExe,
                                    String copyOutDir) throws SystemError {
        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", envs);
        cmd.set("files", COMPILE_FILES);
        // ms-->ns
        cmd.set("cpuLimit", maxCpuTime * 1000 * 1000L);
        cmd.set("clockLimit", maxRealTime * 1000 * 1000L);
        // byte
        cmd.set("memoryLimit", maxMemory);
        cmd.set("procLimit", maxProcessNumber);
        cmd.set("stackLimit", maxStack);

        JSONObject fileContent = new JSONObject();
        fileContent.set("content", code);

        JSONObject copyIn = new JSONObject();
        copyIn.set(srcName, fileContent);

        if (extraFiles != null) {
            for (Map.Entry<String, String> entry : extraFiles.entrySet()) {
                if (!StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue())) {
                    JSONObject content = new JSONObject();
                    content.set("content", entry.getValue());
                    copyIn.set(entry.getKey(), content);
                }
            }
        }

        cmd.set("copyIn", copyIn);
        cmd.set("copyOut", new JSONArray().put("stdout").put("stderr"));

        if (needCopyOutCached) {
            cmd.set("copyOutCached", new JSONArray().put(exeName));
        }

        if (needCopyOutExe) {
            cmd.set("copyOutDir", copyOutDir);
        }

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        JSONArray result = instance.run("/run", param);
        JSONObject compileRes = (JSONObject) result.get(0);
        compileRes.set("originalStatus", compileRes.getStr("status"));
        compileRes.set("status", RESULT_MAP_STATUS.get(compileRes.getStr("status")));
        return result;
    }


    /**
     * @param args            ??????????????????cmd???????????????
     * @param envs            ?????????????????????????????????
     * @param testCasePath    ?????????????????????????????????
     * @param testCaseContent ?????????????????????????????????testCasePath???????????????
     * @param maxTime         ??????????????????????????? ms
     * @param maxOutputSize   ??????????????????????????? kb
     * @param maxStack        ?????????????????????????????? mb
     * @param exeName         ???????????????????????????
     * @param fileId          ???????????????????????????id
     * @param fileContent     ??????????????????????????????????????????userFileId????????????null
     * @MethodName testCase
     * @Description ????????????
     * @Return JSONArray
     */
    public static JSONArray testCase(List<String> args,
                                     List<String> envs,
                                     String testCasePath,
                                     String testCaseContent,
                                     Long maxTime,
                                     Long maxMemory,
                                     Long maxOutputSize,
                                     Integer maxStack,
                                     String exeName,
                                     String fileId,
                                     String fileContent) throws SystemError {

        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", envs);

        JSONArray files = new JSONArray();
        JSONObject content = new JSONObject();
        if (StringUtils.isEmpty(testCasePath)) {
            content.set("content", testCaseContent);
        } else {
            content.set("src", testCasePath);
        }

        JSONObject stdout = new JSONObject();
        stdout.set("name", "stdout");
        stdout.set("max", maxOutputSize);

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * 16);
        files.put(content);
        files.put(stdout);
        files.put(stderr);

        cmd.set("files", files);

        // ms-->ns
        cmd.set("cpuLimit", maxTime * 1000 * 1000L);
        cmd.set("clockLimit", maxTime * 1000 * 1000L * 3);
        // byte
        cmd.set("memoryLimit", (maxMemory + 100) * 1024 * 1024L);
        cmd.set("procLimit", maxProcessNumber);
        cmd.set("stackLimit", maxStack * 1024 * 1024L);

        JSONObject exeFile = new JSONObject();
        if (!StringUtils.isEmpty(fileId)) {
            exeFile.set("fileId", fileId);
        } else {
            exeFile.set("content", fileContent);
        }
        JSONObject copyIn = new JSONObject();
        copyIn.set(exeName, exeFile);

        cmd.set("copyIn", copyIn);
        cmd.set("copyOut", new JSONArray().put("stdout").put("stderr"));

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        // ????????????????????????
        JSONArray result = instance.run("/run", param);

        JSONObject testcaseRes = (JSONObject) result.get(0);
        testcaseRes.set("originalStatus", testcaseRes.getStr("status"));
        testcaseRes.set("status", RESULT_MAP_STATUS.get(testcaseRes.getStr("status")));
        return result;
    }


    /**
     * @param args                   ?????????????????????cmd????????????
     * @param envs                   ?????????????????????????????????
     * @param userOutputFilePath     ?????????????????????????????????
     * @param userOutputFileName     ?????????????????????????????????
     * @param testCaseInputFilePath  ????????????????????????????????????
     * @param testCaseInputFileName  ????????????????????????????????????
     * @param testCaseOutputFilePath ????????????????????????????????????
     * @param testCaseOutputFileName ????????????????????????????????????
     * @param spjExeSrc              ???????????????exe???????????????
     * @param spjExeName             ???????????????exe???????????????
     * @MethodName spjCheckResult
     * @Description ?????????????????????
     * @Return JSONArray
     */
    public static JSONArray spjCheckResult(List<String> args,
                                           List<String> envs,
                                           String userOutputFilePath,
                                           String userOutputFileName,
                                           String testCaseInputFilePath,
                                           String testCaseInputFileName,
                                           String testCaseOutputFilePath,
                                           String testCaseOutputFileName,
                                           String spjExeSrc,
                                           String spjExeName) throws SystemError {

        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", envs);

        JSONArray outFiles = new JSONArray();

        JSONObject content = new JSONObject();
        content.set("content", "");

        JSONObject outStdout = new JSONObject();
        outStdout.set("name", "stdout");
        outStdout.set("max", 1024 * 1024 * 16);

        JSONObject outStderr = new JSONObject();
        outStderr.set("name", "stderr");
        outStderr.set("max", 1024 * 1024 * 16);

        outFiles.put(content);
        outFiles.put(outStdout);
        outFiles.put(outStderr);
        cmd.set("files", outFiles);

        // ms-->ns
        cmd.set("cpuLimit", TIME_LIMIT_MS * 1000 * 1000L);
        cmd.set("clockLimit", TIME_LIMIT_MS * 1000 * 1000L * 3);
        // byte
        cmd.set("memoryLimit", MEMORY_LIMIT_MB * 1024 * 1024L);
        cmd.set("procLimit", maxProcessNumber);
        cmd.set("stackLimit", STACK_LIMIT_MB * 1024 * 1024L);


        JSONObject spjExeFile = new JSONObject();
        spjExeFile.set("src", spjExeSrc);

        JSONObject useOutputFileSrc = new JSONObject();
        useOutputFileSrc.set("src", userOutputFilePath);

        JSONObject stdInputFileSrc = new JSONObject();
        stdInputFileSrc.set("src", testCaseInputFilePath);

        JSONObject stdOutFileSrc = new JSONObject();
        stdOutFileSrc.set("src", testCaseOutputFilePath);

        JSONObject spjCopyIn = new JSONObject();

        spjCopyIn.set(spjExeName, spjExeFile);
        spjCopyIn.set(userOutputFileName, useOutputFileSrc);
        spjCopyIn.set(testCaseInputFileName, stdInputFileSrc);
        spjCopyIn.set(testCaseOutputFileName, stdOutFileSrc);


        cmd.set("copyIn", spjCopyIn);
        cmd.set("copyOut", new JSONArray().put("stdout").put("stderr"));

        JSONObject param = new JSONObject();

        param.set("cmd", new JSONArray().put(cmd));

        // ????????????????????????
        JSONArray result = instance.run("/run", param);
        JSONObject spjRes = (JSONObject) result.get(0);
        spjRes.set("originalStatus", spjRes.getStr("status"));
        spjRes.set("status", RESULT_MAP_STATUS.get(spjRes.getStr("status")));
        return result;
    }


    /**
     * @param args                   cmd??????????????? ?????????????????????
     * @param envs                   ?????????????????????
     * @param userExeName            ?????????????????????
     * @param userFileId             ?????????????????????????????????id????????????????????????????????????????????????
     * @param userFileContent        ????????????????????????????????????userFileId????????????null
     * @param userMaxTime            ????????????????????????????????? ms
     * @param userMaxStack           ???????????????????????????????????? mb
     * @param testCaseInputPath      ?????????????????????????????????
     * @param testCaseInputFileName  ?????????????????????????????????
     * @param testCaseOutputFilePath ?????????????????????????????????
     * @param testCaseOutputFileName ?????????????????????????????????
     * @param userOutputFileName     ?????????????????????????????????
     * @param interactArgs           ?????????????????????cmd????????????
     * @param interactEnvs           ?????????????????????????????????
     * @param interactExeSrc         ???????????????exe????????????
     * @param interactExeName        ???????????????exe????????????
     * @MethodName interactTestCase
     * @Description ????????????
     * @Return JSONArray
     */
    public static JSONArray interactTestCase(List<String> args,
                                             List<String> envs,
                                             String userExeName,
                                             String userFileId,
                                             String userFileContent,
                                             Long userMaxTime,
                                             Long userMaxMemory,
                                             Integer userMaxStack,
                                             String testCaseInputPath,
                                             String testCaseInputFileName,
                                             String testCaseOutputFilePath,
                                             String testCaseOutputFileName,
                                             String userOutputFileName,
                                             List<String> interactArgs,
                                             List<String> interactEnvs,
                                             String interactExeSrc,
                                             String interactExeName) throws SystemError {

        /**
         *  ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         */

        JSONObject pipeInputCmd = new JSONObject();
        pipeInputCmd.set("args", args);
        pipeInputCmd.set("env", envs);

        JSONArray files = new JSONArray();

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);

        files.put(new JSONObject());
        files.put(new JSONObject());
        files.put(stderr);

        String inTmp = files.toString().replace("{}", "null");
        pipeInputCmd.set("files", JSONUtil.parseArray(inTmp, false));

        // ms-->ns
        pipeInputCmd.set("cpuLimit", userMaxTime * 1000 * 1000L);
        pipeInputCmd.set("clockLimit", userMaxTime * 1000 * 1000L * 3);

        // byte

        pipeInputCmd.set("memoryLimit", (userMaxMemory + 100) * 1024 * 1024L);
        pipeInputCmd.set("procLimit", maxProcessNumber);
        pipeInputCmd.set("stackLimit", userMaxStack * 1024 * 1024L);

        JSONObject exeFile = new JSONObject();
        if (!StringUtils.isEmpty(userFileId)) {
            exeFile.set("fileId", userFileId);
        } else {
            exeFile.set("content", userFileContent);
        }
        JSONObject copyIn = new JSONObject();
        copyIn.set(userExeName, exeFile);

        pipeInputCmd.set("copyIn", copyIn);
        pipeInputCmd.set("copyOut", new JSONArray());


        // ???????????????????????????????????????????????????????????????????????????????????????????????????
        JSONObject pipeOutputCmd = new JSONObject();
        pipeOutputCmd.set("args", interactArgs);
        pipeOutputCmd.set("env", interactEnvs);

        JSONArray outFiles = new JSONArray();


        JSONObject outStderr = new JSONObject();
        outStderr.set("name", "stderr");
        outStderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        outFiles.put(new JSONObject());
        outFiles.put(new JSONObject());
        outFiles.put(outStderr);
        String outTmp = outFiles.toString().replace("{}", "null");
        pipeOutputCmd.set("files", JSONUtil.parseArray(outTmp, false));

        // ms-->ns
        pipeOutputCmd.set("cpuLimit", userMaxTime * 1000 * 1000L * 2);
        pipeOutputCmd.set("clockLimit", userMaxTime * 1000 * 1000L * 3 * 2);
        // byte
        pipeOutputCmd.set("memoryLimit", (userMaxMemory + 100) * 1024 * 1024L * 2);
        pipeOutputCmd.set("procLimit", maxProcessNumber);
        pipeOutputCmd.set("stackLimit", STACK_LIMIT_MB * 1024 * 1024L);

        JSONObject spjExeFile = new JSONObject();
        spjExeFile.set("src", interactExeSrc);

        JSONObject stdInputFileSrc = new JSONObject();
        stdInputFileSrc.set("src", testCaseInputPath);

        JSONObject stdOutFileSrc = new JSONObject();
        stdOutFileSrc.set("src", testCaseOutputFilePath);

        JSONObject interactiveCopyIn = new JSONObject();
        interactiveCopyIn.set(interactExeName, spjExeFile);
        interactiveCopyIn.set(testCaseInputFileName, stdInputFileSrc);
        interactiveCopyIn.set(testCaseOutputFileName, stdOutFileSrc);


        pipeOutputCmd.set("copyIn", interactiveCopyIn);
        pipeOutputCmd.set("copyOut", new JSONArray().put(userOutputFileName));

        JSONArray cmdList = new JSONArray();
        cmdList.put(pipeInputCmd);
        cmdList.put(pipeOutputCmd);

        JSONObject param = new JSONObject();
        // ??????cmd??????
        param.set("cmd", cmdList);

        // ??????????????????
        JSONArray pipeMapping = new JSONArray();
        // ????????????
        JSONObject user = new JSONObject();

        JSONObject userIn = new JSONObject();
        userIn.set("index", 0);
        userIn.set("fd", 1);

        JSONObject userOut = new JSONObject();
        userOut.set("index", 1);
        userOut.set("fd", 0);

        user.set("in", userIn);
        user.set("out", userOut);
        user.set("max", STDIO_SIZE_MB * 1024 * 1024);
        user.set("proxy", true);
        user.set("name", "stdout");

        // ????????????
        JSONObject judge = new JSONObject();

        JSONObject judgeIn = new JSONObject();
        judgeIn.set("index", 1);
        judgeIn.set("fd", 1);

        JSONObject judgeOut = new JSONObject();
        judgeOut.set("index", 0);
        judgeOut.set("fd", 0);

        judge.set("in", judgeIn);
        judge.set("out", judgeOut);
        judge.set("max", STDIO_SIZE_MB * 1024 * 1024);
        judge.set("proxy", true);
        judge.set("name", "stdout");


        // ???????????????????????????
        pipeMapping.add(user);
        pipeMapping.add(judge);

        param.set("pipeMapping", pipeMapping);

        // ????????????????????????
        JSONArray result = instance.run("/run", param);
        JSONObject userRes = (JSONObject) result.get(0);
        JSONObject interactiveRes = (JSONObject) result.get(1);
        userRes.set("originalStatus", userRes.getStr("status"));
        userRes.set("status", RESULT_MAP_STATUS.get(userRes.getStr("status")));
        interactiveRes.set("originalStatus", interactiveRes.getStr("status"));
        interactiveRes.set("status", RESULT_MAP_STATUS.get(interactiveRes.getStr("status")));
        return result;
    }

}
 /*
     1. compile
        Json Request Body
        {
            "cmd": [{
                "args": ["/usr/bin/g++", "a.cc", "-o", "a"],
                "env": ["PATH=/usr/bin:/bin"],
                "files": [{
                    "content": ""
                }, {
                    "name": "stdout",
                    "max": 10240
                }, {
                    "name": "stderr",
                    "max": 10240
                }],
                "cpuLimit": 10000000000,
                "memoryLimit": 104857600,
                "procLimit": 50,
                "copyIn": {
                    "a.cc": {
                        "content": "#include <iostream>\nusing namespace std;\nint main() {\nint a, b;\ncin >> a >> b;\ncout << a + b << endl;\n}"
                    }
                },
                "copyOut": ["stdout", "stderr"],
                "copyOutCached": ["a.cc", "a"],
                "copyOutDir": "1"
            }]
        }

        Json Response Data

        [
            {
                "status": "Accepted",
                "exitStatus": 0,
                "time": 303225231,
                "memory": 32243712,
                "runTime": 524177700,
                "files": {
                    "stderr": "",
                    "stdout": ""
                },
                "fileIds": {
                    "a": "5LWIZAA45JHX4Y4Z",
                    "a.cc": "NOHPGGDTYQUFRSLJ"
                }
            }
        ]
    2.test case

            Json Request Body
              {
                "cmd": [{
                    "args": ["a"],
                    "env": ["PATH=/usr/bin:/bin","LANG=en_US.UTF-8","LC_ALL=en_US.UTF-8","LANGUAGE=en_US:en"],
                    "files": [{
                        "src": "/judge/test_case/problem_1010/1.in"
                    }, {
                        "name": "stdout",
                        "max": 10240
                    }, {
                        "name": "stderr",
                        "max": 10240
                    }],
                    "cpuLimit": 10000000000,
                    "realCpuLimit":30000000000,
                    "stackLimit":134217728,
                    "memoryLimit": 104811111,
                    "procLimit": 50,
                    "copyIn": {
                        "a":{"fileId":"WDQL5TNLRRVB2KAP"}
                    },
                    "copyOut": ["stdout", "stderr"]
                }]
            }

            Json Response Data
             [{
              "status": "Accepted",
              "exitStatus": 0,
              "time": 3171607,
              "memory": 475136,
              "runTime": 110396333,
              "files": {
                "stderr": "",
                "stdout": "23\n"
              }
            }]

    3. Interactive

        {
    "pipeMapping": [
        {
            "in": {
                "max": 16777216,
                "index": 0,
                "fd": 1
            },
            "out": {
                "index": 1,
                "fd": 0
            }
        }
    ],
    "cmd": [
        {
            "stackLimit": 134217728,
            "cpuLimit": 3000000000,
            "realCpuLimit": 9000000000,
            "clockLimit": 64,
            "env": [
                "LANG=en_US.UTF-8",
                "LANGUAGE=en_US:en",
                "LC_ALL=en_US.UTF-8",
                "PYTHONIOENCODING=utf-8"
            ],
            "copyOut": [
                "stderr"
            ],
            "args": [
                "/usr/bin/python3",
                "main"
            ],
            "files": [
                {
                    "src": "/judge/test_case/problem_1002/5.in"
                },
                null,
                {
                    "max": 16777216,
                    "name": "stderr"
                }
            ],
            "memoryLimit": 536870912,
            "copyIn": {
                "main": {
                    "fileId": "CGTRDEMKW5VAYN6O"
                }
            }
        },
        {
            "stackLimit": 134217728,
            "cpuLimit": 8000000000,
            "clockLimit": 24000000000,
            "env": [
                "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                "LANG=en_US.UTF-8",
                "LANGUAGE=en_US:en",
                "LC_ALL=en_US.UTF-8"
            ],
            "copyOut": [
                "stdout",
                "stderr"
            ],
            "args": [
                "/w/spj",
                "/w/tmp"
            ],
            "files": [
                null,
                {
                    "max": 16777216,
                    "name": "stdout"
                },
                {
                    "max": 16777216,
                    "name": "stderr"
                }
            ],
            "memoryLimit": 536870912,
            "copyIn": {
                "spj": {
                    "src": "/judge/spj/1002/spj"
                },
                "tmp": {
                    "src": "/judge/test_case/problem_1002/5.out"
                }
            },
            "procLimit": 64
        }
    ]
}


  */
