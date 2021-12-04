package com.dpworld.core.dataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static com.dpworld.core.commons.Constants.PATH_TO_DATA_SET;
import static com.dpworld.core.commons.Constants.PATH_TO_TEST_CAPS_JSON;

public class TestData {

    private static final Logger log = LogManager.getLogger(TestData.class);

    public static final ThreadLocal<Properties> prop = new ThreadLocal<>();
    public String capsPath;
    public String dataPath;


    public TestData(String env,String pathData,String pathCaps)  {

        if(pathCaps == null || pathCaps.isEmpty()) {
            capsPath = PATH_TO_TEST_CAPS_JSON + env.toLowerCase() + ".json";
        } else  {
            capsPath = pathCaps;
        }

        if(pathData == null || pathData.isEmpty()) {
            dataPath = PATH_TO_DATA_SET + env.toLowerCase() + ".properties";
        } else {
            dataPath = pathData;
        }
        setTestsProperties();
    }

    public TestData(String env)  {

        capsPath = PATH_TO_TEST_CAPS_JSON + env.toLowerCase() + ".json";
        dataPath = PATH_TO_DATA_SET + env.toLowerCase() + ".properties";

        setTestsProperties();
    }

    public void setTestsProperties()  {

        try {
            Properties dataSet = new Properties();
            dataSet.load(new FileReader(dataPath));
            prop.set(dataSet);
        } catch (IOException e) {
            log.info("Properties not loaded properly ...");
        }
    }


    public String getProperty(String property) {
        String data = null;
        try {
            data = prop.get().getProperty(property);
        } catch (Exception e) {
            log.error("Property Not Found");
        }
        return data;
    }

    public JSONObject getTestsCapsConfig() throws IOException, ParseException {
            JSONParser parser = new JSONParser();
            JSONObject testCapsConfig = (JSONObject) parser.parse(new FileReader(capsPath));
            return testCapsConfig;
    }

    public String getUsername(JSONObject testCapsConfig) {
        String username = System.getenv("BROWSERSTACK_USERNAME");
        if (username == null) {
            username = testCapsConfig.get("user").toString();
        }
        return username;
    }

    public String getAccessKey(JSONObject testCapsConfig) {
        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (accessKey == null) {
            accessKey = testCapsConfig.get("key").toString();
        }
        return accessKey;
    }

    public String getBuildId(String buildId) {
        if(buildId == null || buildId.equalsIgnoreCase("1")) {
            return java.time.LocalDate.now().toString();
        }
        return buildId;
    }


}
