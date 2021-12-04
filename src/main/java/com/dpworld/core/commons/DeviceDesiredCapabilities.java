package com.dpworld.core.commons;

import com.dpworld.core.utils.DeviceType;
import com.dpworld.core.utils.ExecutionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Locale;
import java.util.Random;

import static com.dpworld.core.commons.Constants.BROWSERSTACK_URL;
import static com.dpworld.core.commons.Constants.PATH_TO_TEST_DEVICES_CAPS_JSON;

public class DeviceDesiredCapabilities {

    private static final Logger log = LogManager.getLogger(DeviceDesiredCapabilities.class);

    public int getDeviceIndexForCapabilities(String deviceType) {

        int index = -1;

        switch (DeviceType.valueOf(deviceType.toUpperCase())) {
            case CHROME_92:
            case SAMSUNG_S20:
            case IPHONE_12:
                index = 0;
                break;
            case FIREFOX_90:
            case SAMSUNG_S21_ULTRA:
            case IPHONE_11_PRO:
                index = 1;
                break;
            case EDGE_92:
            case SAMSUNG_GALAXY_S9_PLUS:
            case IPHONE_XS:
                index = 2;
                break;
            case IE_11:
            case SAFARI_14:
            case SAMSUNG_GALAXY_NOTE_9:
            case IPHONE_X:
                index = 3;
                break;
        }


        if(index == -1) {
            Random random = new Random();
            index = random.nextInt(4);
        }

        return index;
    }


    public JSONObject getJSONObjectFromFile(String os,String devicePath) {

        JSONParser parser = new JSONParser();
        String finalPath = null;

        if(devicePath == null || devicePath.isEmpty() || !devicePath.contains("devices")) {
            finalPath = PATH_TO_TEST_DEVICES_CAPS_JSON;
        }

        if(devicePath.toLowerCase().contains(os.toLowerCase()+".properties")){
            finalPath = devicePath;
        }

        String path = finalPath + os.toLowerCase() + ".json";

        JSONObject jsonObject = null;

        log.info("Reading for os -> {} and from file -> {} " , os,path);

        try {
            JSONObject testConfig = (JSONObject) parser.parse(new FileReader(path)) ;

            switch (ExecutionType.valueOf(os.toUpperCase())) {
                case WINDOWS:
                    jsonObject = (JSONObject)  testConfig.get("windows");
                    break;
                case OSX:
                    jsonObject = (JSONObject)  testConfig.get("osx");
                    break;
                case ANDROID:
                    jsonObject = (JSONObject)  testConfig.get("android");
                    break;
                case IOS:
                    jsonObject = (JSONObject)  testConfig.get("ios");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid os type defined . Stopping execution : " + os);

            }
        } catch (Exception e) {
            log.error("Not able to read the file ... Stopping execution with stack trace : {}", e.getMessage());
        }

        return jsonObject;
    }


    public static String getBrowserStackPath(String username, String accessKey) {
        String url = "https://" + username + ":" + accessKey + BROWSERSTACK_URL;
        return url;
    }
}
