package com.dpworld.core.factory;

import com.browserstack.local.Local;
import com.dpworld.core.commons.DeviceDesiredCapabilities;
import com.dpworld.core.dataProviders.TestData;
import com.dpworld.core.entities.ParamsEntity;
import com.dpworld.core.helpers.BrowserStackHelper;
import com.dpworld.core.utils.AppType;
import com.dpworld.core.utils.ExecutionType;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.dpworld.core.commons.Constants.*;

public class BrowserStackDriver implements Driver {

    private static final Logger log = LogManager.getLogger(BrowserStackDriver.class);
    DeviceDesiredCapabilities desiredCapabilities;
    BrowserStackHelper browserStackHelper;
    Local local;
    TestData testData;

    @Override
    public WebDriver setupDriver(ParamsEntity entity) {
        return null;
    }

    @SneakyThrows
    @Override
    public WebDriver setupDriver(ParamsEntity entity,String dataPath, String capsPath) {

        WebDriver driver = null;
        String os = entity.getOs();
        String testType = entity.getTestType();
        int env_cap_id = entity.getEnv_cap_id();
        JSONObject envs = new JSONObject();
        JSONObject profilesJson;
        testData = new TestData(entity.getEnv(),dataPath,capsPath);
        JSONObject testCapsConfig = testData.getTestsCapsConfig();
        String url = (String) testCapsConfig.get("application_endpoint");

        if (!os.equalsIgnoreCase("none")) {
            if (os.equalsIgnoreCase(ExecutionType.ANDROID.name()) || os.equalsIgnoreCase(ExecutionType.IOS.name())
                    || os.equalsIgnoreCase(ExecutionType.WINDOWS.name()) || os.equalsIgnoreCase(ExecutionType.OSX.name())) {
                testType = os.toLowerCase();
                desiredCapabilities = new DeviceDesiredCapabilities();
                if(entity.getDevice() == null || entity.getDevice().isEmpty()) {

                } else {
                    env_cap_id = desiredCapabilities.getDeviceIndexForCapabilities(entity.getDevice());
                }
                envs = desiredCapabilities.getJSONObjectFromFile(os,capsPath);
            }
        } else {
            profilesJson = (JSONObject) testCapsConfig.get("tests");
            envs = (JSONObject) profilesJson.get(testType);
        }

        Map<String, String> commonCapabilities = (Map<String, String>) envs.get("common_caps");

        log.info("Commons caps are : {}", commonCapabilities);

        commonCapabilities.put("name", entity.getBuild());
        commonCapabilities.put("build", entity.getBuild());

        Map<String, String> envCapabilities = (Map<String, String>) ((org.json.simple.JSONArray) envs.get("env_caps")).get(env_cap_id);

        log.info("Env Capabilities are : {} ", envCapabilities);

        Map<String, String> localCapabilities = (Map<String, String>) envs.get("local_binding_caps");
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.merge(new DesiredCapabilities(commonCapabilities));
        caps.merge(new DesiredCapabilities(envCapabilities));
        if (testType.equals("local")) {
            url = (String) envs.get("application_endpoint");
            caps.merge(new DesiredCapabilities(localCapabilities));
        }

        String userName = testData.getUsername(testCapsConfig);
        String accessKey = testData.getAccessKey(testCapsConfig);

        caps.setCapability("browserstack.user", userName);
        caps.setCapability("browserstack.key", accessKey);
        caps.setCapability("browserstack.autoWait", "5");

        createSecureTunnelIfNeeded(caps, testCapsConfig);

        /**
         *  Uploading the apk/ipa to browserstack and get the remote driver .
         */

        if (entity.isApp()) {
            browserStackHelper = new BrowserStackHelper();
            File  directoryPath = new File(PATH_TO_APK);
            File filesList[] = directoryPath.listFiles();
            String ipa = null;
            String apk = null;
            for(File file: filesList) {
                if(file.getName().toLowerCase().contains("ipa")) {
                    ipa = file.getPath();
                } else if (file.getName().toLowerCase().contains("apk")) {
                    apk = file.getPath();
                }
            }
            if (entity.getAppType().equalsIgnoreCase(AppType.ANDROID.name())) {
                try {
                    if(apk == null) {
                        log.error("App is not present in path. Exiting executin ");
                        System.exit(0);
                    }
                    String app_url = browserStackHelper.uploadAppInDevice(userName, accessKey, BROWSERSTACK_APP_UPLOAD, apk);
                    caps.setCapability("app", app_url);
                    log.info("Setting Up Android Driver");
                    driver =  new AndroidDriver<AndroidElement>(new URL(BROWSERSTACK_HUB_URL), caps);
                } catch (Exception e) {
                    log.error("APK Not Uploaded.");
                }
            } else if (entity.getAppType().equalsIgnoreCase(AppType.IOS.name())) {
                try {
                    if(ipa == null) {
                        log.error("Ipa is not present in path. Exiting executin ");
                        System.exit(0);
                    }
                    String app_url = browserStackHelper.uploadAppInDevice(userName, accessKey, BROWSERSTACK_APP_UPLOAD, ipa);
                    caps.setCapability("app", app_url);
                    log.info("Setting Up IOS Driver");
                    driver =  new IOSDriver<IOSElement>(new URL(BROWSERSTACK_HUB_URL), caps);
                } catch (Exception e) {
                    log.error("IPA Not Uploaded.");
                }
            } else {
                log.error("DeviceType is wrong. Exiting !!! ");
                System.exit(0);
            }
        } else {
            log.info("Final Capabilites are  : {}", caps);
            driver =  new RemoteWebDriver(new URL(BROWSERSTACK_HUB_URL), caps);
        }

        return driver;
    }

    private void createSecureTunnelIfNeeded(DesiredCapabilities caps, JSONObject testCapsConfig) throws Exception {
        if (caps.getCapability("browserstack.local") != null
                && caps.getCapability("browserstack.local").equals("true")) {
            local = new Local();
            UUID uuid = UUID.randomUUID();
            caps.setCapability("browserstack.localIdentifier", uuid.toString());
            Map<String, String> options = new HashMap<>();
            options.put("key", testData.getAccessKey(testCapsConfig));
            options.put("localIdentifier", uuid.toString());
            local.start(options);
        }
    }

    public void localSetupCleanUp() throws Exception {
        if (local != null) {
            local.stop();
        }
    }

}
