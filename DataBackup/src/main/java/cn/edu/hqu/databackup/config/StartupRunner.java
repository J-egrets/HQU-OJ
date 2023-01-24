package cn.edu.hqu.databackup.config;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import cn.edu.hqu.databackup.crawler.language.LanguageContext;
import cn.edu.hqu.databackup.dao.judge.RemoteJudgeAccountEntityService;
import cn.edu.hqu.databackup.dao.problem.LanguageEntityService;
import cn.edu.hqu.databackup.manager.admin.system.ConfigManager;
import cn.edu.hqu.api.pojo.entity.judge.RemoteJudgeAccount;
import cn.edu.hqu.api.pojo.entity.problem.Language;
import cn.edu.hqu.databackup.pojo.vo.ConfigVO;
import cn.edu.hqu.databackup.utils.Constants;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author egret
 * @Description:项目启动后，初始化运行该run方法
 */
@Component
@Slf4j(topic = "hoj")
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private ConfigVO configVo;

    @Autowired
    private ConfigManager configManager;

    @Autowired
    private NacosSwitchConfig nacosSwitchConfig;

    @Autowired
    private RemoteJudgeAccountEntityService remoteJudgeAccountEntityService;

    @Autowired
    private LanguageEntityService languageEntityService;

    @Value("${open-remote-judge}")
    private String openRemoteJudge;

    // jwt配置
    @Value("${jwt-token-secret}")
    private String tokenSecret;

    @Value("${jwt-token-expire}")
    private String tokenExpire;

    @Value("${jwt-token-fresh-expire}")
    private String checkRefreshExpire;

    // 数据库配置
    @Value("${mysql-username}")
    private String mysqlUsername;

    @Value("${mysql-password}")
    private String mysqlPassword;

    @Value("${mysql-name}")
    private String mysqlDBName;

    @Value("${mysql-host}")
    private String mysqlHost;

    @Value("${mysql-public-host}")
    private String mysqlPublicHost;

    @Value("${mysql-port}")
    private Integer mysqlPort;

    @Value("${mysql-public-port}")
    private Integer mysqlPublicPort;

    // 缓存配置
    @Value("${redis-host}")
    private String redisHost;

    @Value("${redis-port}")
    private Integer redisPort;

    @Value("${redis-password}")
    private String redisPassword;
    // 判题服务token
    @Value("${judge-token}")
    private String judgeToken;

    // 邮箱配置
    @Value("${email-username}")
    private String emailUsername;

    @Value("${email-password}")
    private String emailPassword;

    @Value("${email-host}")
    private String emailHost;

    @Value("${email-port}")
    private Integer emailPort;

    @Value("${hdu-username-list}")
    private List<String> hduUsernameList;

    @Value("${hdu-password-list}")
    private List<String> hduPasswordList;

    @Value("${cf-username-list}")
    private List<String> cfUsernameList;

    @Value("${cf-password-list}")
    private List<String> cfPasswordList;

    @Value("${poj-username-list}")
    private List<String> pojUsernameList;

    @Value("${poj-password-list}")
    private List<String> pojPasswordList;

    @Value("${atcoder-username-list}")
    private List<String> atcoderUsernameList;

    @Value("${atcoder-password-list}")
    private List<String> atcoderPasswordList;

    @Value("${spoj-username-list}")
    private List<String> spojUsernameList;

    @Value("${spoj-password-list}")
    private List<String> spojPasswordList;

    @Value("${forced-update-remote-judge-account}")
    private Boolean forcedUpdateRemoteJudgeAccount;

    @Override
    public void run(String... args) throws Exception {

        // 修改nacos上的默认、web、switch配置文件
        initDefaultConfig();

        initWebConfig();

        initSwitchConfig();

//      upsertHOJLanguage("PHP", "PyPy2", "PyPy3", "JavaScript Node", "JavaScript V8");
//      checkAllLanguageUpdate();
    }


    /**
     * 更新修改基础的配置
     */
    private void initDefaultConfig() {
        if (judgeToken.equals("default")) {
            configVo.setJudgeToken(IdUtil.fastSimpleUUID());
        } else {
            configVo.setJudgeToken(judgeToken);
        }

        if (tokenSecret.equals("default")) {
            if (StrUtil.isBlank(configVo.getTokenSecret())) {
                configVo.setTokenSecret(IdUtil.fastSimpleUUID());
            }
        } else {
            configVo.setTokenSecret(tokenSecret);
        }
        configVo.setTokenExpire(tokenExpire);
        configVo.setCheckRefreshExpire(checkRefreshExpire);

        configVo.setMysqlUsername(mysqlUsername);
        configVo.setMysqlPassword(mysqlPassword);
        configVo.setMysqlHost(mysqlHost);
        configVo.setMysqlPublicHost(mysqlPublicHost);
        configVo.setMysqlPort(mysqlPort);
        configVo.setMysqlPublicPort(mysqlPublicPort);
        configVo.setMysqlDBName(mysqlDBName);

        configVo.setRedisHost(redisHost);
        configVo.setRedisPort(redisPort);
        configVo.setRedisPassword(redisPassword);

        configManager.sendNewConfigToNacos();
    }


    private void initWebConfig() {
        WebConfig webConfig = nacosSwitchConfig.getWebConfig();
        boolean isChanged = false;
        if (!Objects.equals(webConfig.getEmailHost(), emailHost)
                && (webConfig.getEmailHost() == null || !"your_email_host".equals(emailHost))) {
            webConfig.setEmailHost(emailHost);
            isChanged = true;
        }
        if (!Objects.equals(webConfig.getEmailPort(), emailPort)
                && (webConfig.getEmailPort() == null || emailPort != 456)) {
            webConfig.setEmailPort(emailPort);
            isChanged = true;
        }
        if (!Objects.equals(webConfig.getEmailUsername(), emailUsername)
                && (webConfig.getEmailUsername() == null || !"your_email_username".equals(emailUsername))) {
            webConfig.setEmailUsername(emailUsername);
            isChanged = true;
        }
        if (!Objects.equals(webConfig.getEmailPassword(), emailPassword)
                && (webConfig.getEmailPassword() == null || !"your_email_password".equals(emailPassword))) {
            webConfig.setEmailPassword(emailPassword);
            isChanged = true;
        }
        if (isChanged) {
            nacosSwitchConfig.publishWebConfig();
        }
    }

    private void initSwitchConfig() {

        SwitchConfig switchConfig = nacosSwitchConfig.getSwitchConfig();

        boolean isChanged = false;
        if ((CollectionUtils.isEmpty(switchConfig.getHduUsernameList())
                && !CollectionUtils.isEmpty(hduUsernameList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setHduUsernameList(hduUsernameList);
            isChanged = true;
        }

        if ((CollectionUtils.isEmpty(switchConfig.getHduPasswordList())
                && !CollectionUtils.isEmpty(hduPasswordList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setHduPasswordList(hduPasswordList);
            isChanged = true;
        }

        if ((CollectionUtils.isEmpty(switchConfig.getCfUsernameList())
                && !CollectionUtils.isEmpty(cfUsernameList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setCfUsernameList(cfUsernameList);
            isChanged = true;
        }

        if ((CollectionUtils.isEmpty(switchConfig.getCfPasswordList())
                && !CollectionUtils.isEmpty(cfPasswordList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setCfPasswordList(cfPasswordList);
            isChanged = true;
        }

        if ((CollectionUtils.isEmpty(switchConfig.getPojUsernameList())
                && !CollectionUtils.isEmpty(pojUsernameList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setPojUsernameList(pojUsernameList);
            isChanged = true;
        }

        if ((CollectionUtils.isEmpty(switchConfig.getPojPasswordList())
                && !CollectionUtils.isEmpty(pojPasswordList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setPojPasswordList(pojPasswordList);
            isChanged = true;
        }

        if ((CollectionUtils.isEmpty(switchConfig.getAtcoderUsernameList())
                && !CollectionUtils.isEmpty(atcoderUsernameList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setAtcoderUsernameList(atcoderUsernameList);
            isChanged = true;
        }

        if ((CollectionUtils.isEmpty(switchConfig.getAtcoderPasswordList())
                && !CollectionUtils.isEmpty(atcoderPasswordList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setAtcoderPasswordList(atcoderPasswordList);
            isChanged = true;
        }

        if ((CollectionUtils.isEmpty(switchConfig.getSpojUsernameList())
                && !CollectionUtils.isEmpty(spojUsernameList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setSpojUsernameList(spojUsernameList);
            isChanged = true;
        }

        if ((CollectionUtils.isEmpty(switchConfig.getSpojPasswordList())
                && !CollectionUtils.isEmpty(spojPasswordList))
                || forcedUpdateRemoteJudgeAccount) {
            switchConfig.setSpojPasswordList(spojPasswordList);
            isChanged = true;
        }

        if (isChanged) {
            nacosSwitchConfig.publishWebConfig();
        }

        if (openRemoteJudge.equals("true")) {
            // 初始化清空表
            remoteJudgeAccountEntityService.remove(new QueryWrapper<>());
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.HDU.getName(),
                    switchConfig.getHduUsernameList(),
                    switchConfig.getHduPasswordList());
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.POJ.getName(),
                    switchConfig.getPojUsernameList(),
                    switchConfig.getPojPasswordList());
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.CODEFORCES.getName(),
                    switchConfig.getCfUsernameList(),
                    switchConfig.getCfPasswordList());
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.SPOJ.getName(),
                    switchConfig.getSpojUsernameList(),
                    switchConfig.getSpojPasswordList());
            addRemoteJudgeAccountToMySQL(Constants.RemoteOJ.ATCODER.getName(),
                    switchConfig.getAtcoderUsernameList(),
                    switchConfig.getAtcoderPasswordList());
            checkRemoteOJLanguage(Constants.RemoteOJ.SPOJ, Constants.RemoteOJ.ATCODER);
        }
    }


    /**
     * @param oj
     * @param usernameList
     * @param passwordList
     * @MethodName addRemoteJudgeAccountToRedis
     * @Description 将传入的对应oj账号写入到mysql
     * @Return
     */
    private void addRemoteJudgeAccountToMySQL(String oj, List<String> usernameList, List<String> passwordList) {

        if (CollectionUtils.isEmpty(usernameList) || CollectionUtils.isEmpty(passwordList) || usernameList.size() != passwordList.size()) {
            log.error("[Init System Config] [{}]: There is no account or password configured for remote judge, " +
                            "username list:{}, password list:{}", oj, Arrays.toString(usernameList.toArray()),
                    Arrays.toString(passwordList.toArray()));
        }

        List<RemoteJudgeAccount> remoteAccountList = new LinkedList<>();
        for (int i = 0; i < usernameList.size(); i++) {
            remoteAccountList.add(new RemoteJudgeAccount()
                    .setUsername(usernameList.get(i))
                    .setPassword(passwordList.get(i))
                    .setStatus(true)
                    .setVersion(0L)
                    .setOj(oj));

        }

        if (remoteAccountList.size() > 0) {
            boolean addOk = remoteJudgeAccountEntityService.saveOrUpdateBatch(remoteAccountList);
            if (!addOk) {
                log.error("[Init System Config] Remote judge initialization failed. Failed to add account for: [{}]. Please check the configuration file and restart!", oj);
            }
        }
    }


    @Deprecated
    private void upsertHOJLanguage(String... languageList) {
        /**
         * 新增js、pypy、php语言
         */
        for (String language : languageList) {
            QueryWrapper<Language> languageQueryWrapper = new QueryWrapper<>();
            languageQueryWrapper.eq("oj", "ME")
                    .eq("name", language);
            int count = languageEntityService.count(languageQueryWrapper);
            if (count == 0) {
                Language newLanguage = buildHOJLanguage(language);
                boolean isOk = languageEntityService.save(newLanguage);
                if (!isOk) {
                    log.error("[Init System Config] [HOJ] Failed to add new language [{}]! Please check whether the language table corresponding to the database has the language!", language);
                }
            }
        }
    }

    @Deprecated
    private void checkAllLanguageUpdate() {

        UpdateWrapper<Language> languageUpdateWrapper = new UpdateWrapper<>();
        languageUpdateWrapper.eq("oj", "ME")
                .eq("name", "Python3")
                .set("description", "Python 3.7.5");
        languageEntityService.update(languageUpdateWrapper);

        /**
         * 删除cf的Microsoft Visual C++ 2010
         */
        UpdateWrapper<Language> deleteWrapper = new UpdateWrapper<>();
        deleteWrapper.eq("name", "Microsoft Visual C++ 2010")
                .eq("oj", "CF");
        languageEntityService.remove(deleteWrapper);

        /**
         * 增加hdu的Java和C#支持
         */
        List<Language> newHduLanguageList = new ArrayList<>();
        QueryWrapper<Language> languageQueryWrapper = new QueryWrapper<>();
        languageQueryWrapper.select("id", "name");
        languageQueryWrapper.eq("oj", "HDU");
        List<Language> hduLanguageList = languageEntityService.list(languageQueryWrapper);
        List<String> collect = hduLanguageList.stream()
                .map(Language::getName)
                .collect(Collectors.toList());
        if (!collect.contains("Java")) {
            Language hduJavaLanguage = new Language();
            hduJavaLanguage.setContentType("text/x-java")
                    .setName("Java")
                    .setDescription("Java")
                    .setIsSpj(false)
                    .setOj("HDU");
            newHduLanguageList.add(hduJavaLanguage);
        }
        if (!collect.contains("C#")) {
            Language hduCSharpLanguage = new Language();
            hduCSharpLanguage.setContentType("text/x-csharp")
                    .setName("C#")
                    .setDescription("C#")
                    .setIsSpj(false)
                    .setOj("HDU");
            newHduLanguageList.add(hduCSharpLanguage);
        }
        if (newHduLanguageList.size() > 0) {
            languageEntityService.saveBatch(newHduLanguageList);
        }
    }

    private void checkRemoteOJLanguage(Constants.RemoteOJ... remoteOJList) {
        for (Constants.RemoteOJ remoteOJ : remoteOJList) {
            QueryWrapper<Language> languageQueryWrapper = new QueryWrapper<>();
            languageQueryWrapper.eq("oj", remoteOJ.getName());
            int count = languageEntityService.count(languageQueryWrapper);
            if (count == 0) {
                List<Language> languageList = new LanguageContext(remoteOJ).buildLanguageList();
                boolean isOk = languageEntityService.saveBatch(languageList);
                if (!isOk) {
                    log.error("[Init System Config] [{}] Failed to initialize language list! Please check whether the language table corresponding to the database has the OJ language!", remoteOJ.getName());
                }
            }
        }
    }

    private Language buildHOJLanguage(String lang) {
        Language language = new Language();
        switch (lang) {
            case "PHP":
                language.setName("PHP")
                        .setCompileCommand("/usr/bin/php {src_path}")
                        .setContentType("text/x-php")
                        .setDescription("PHP 7.3.33")
                        .setTemplate("<?=array_sum(fscanf(STDIN, \"%d %d\"));")
                        .setIsSpj(false)
                        .setOj("ME");
                return language;
            case "JavaScript Node":
                language.setName("JavaScript Node")
                        .setCompileCommand("/usr/bin/node {src_path}")
                        .setContentType("text/javascript")
                        .setDescription("Node.js 14.19.0")
                        .setTemplate("var readline = require('readline');\n" +
                                "const rl = readline.createInterface({\n" +
                                "        input: process.stdin,\n" +
                                "        output: process.stdout\n" +
                                "});\n" +
                                "rl.on('line', function(line){\n" +
                                "   var tokens = line.split(' ');\n" +
                                "    console.log(parseInt(tokens[0]) + parseInt(tokens[1]));\n" +
                                "});")
                        .setIsSpj(false)
                        .setOj("ME");
                return language;
            case "JavaScript V8":
                language.setName("JavaScript V8")
                        .setCompileCommand("/usr/bin/jsv8/d8 {src_path}")
                        .setContentType("text/javascript")
                        .setDescription("JavaScript V8 8.4.109")
                        .setTemplate("const [a, b] = readline().split(' ').map(n => parseInt(n, 10));\n" +
                                "print((a + b).toString());")
                        .setIsSpj(false)
                        .setOj("ME");
                return language;
            case "PyPy2":
                language.setName("PyPy2")
                        .setContentType("text/x-python")
                        .setCompileCommand("/usr/bin/pypy -m py_compile {src_path}")
                        .setDescription("PyPy 2.7.18 (7.3.8)")
                        .setTemplate("print sum(int(x) for x in raw_input().split(' '))")
                        .setCodeTemplate("//PREPEND BEGIN\n" +
                                "//PREPEND END\n" +
                                "\n" +
                                "//TEMPLATE BEGIN\n" +
                                "def add(a, b):\n" +
                                "    return a + b\n" +
                                "//TEMPLATE END\n" +
                                "\n" +
                                "\n" +
                                "if __name__ == '__main__':  \n" +
                                "    //APPEND BEGIN\n" +
                                "    a, b = 1, 1\n" +
                                "    print add(a, b)\n" +
                                "    //APPEND END")
                        .setIsSpj(false)
                        .setOj("ME");
                return language;
            case "PyPy3":
                language.setName("PyPy3")
                        .setContentType("text/x-python")
                        .setDescription("PyPy 3.8.12 (7.3.8)")
                        .setCompileCommand("/usr/bin/pypy3 -m py_compile {src_path}")
                        .setTemplate("print(sum(int(x) for x in input().split(' ')))")
                        .setCodeTemplate("//PREPEND BEGIN\n" +
                                "//PREPEND END\n" +
                                "\n" +
                                "//TEMPLATE BEGIN\n" +
                                "def add(a, b):\n" +
                                "    return a + b\n" +
                                "//TEMPLATE END\n" +
                                "\n" +
                                "\n" +
                                "if __name__ == '__main__':  \n" +
                                "    //APPEND BEGIN\n" +
                                "    a, b = 1, 1\n" +
                                "    print(add(a, b))\n" +
                                "    //APPEND END")
                        .setIsSpj(false)
                        .setOj("ME");
                return language;
        }
        return null;
    }

}

