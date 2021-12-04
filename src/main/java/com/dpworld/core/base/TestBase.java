package com.dpworld.core.base;

import com.dpworld.core.dataProviders.TestData;
import com.dpworld.core.entities.ParamsEntity;
import com.dpworld.core.factory.BrowserStackDriver;
import com.dpworld.core.factory.Driver;
import com.dpworld.core.factory.DriverFactory;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;

public class TestBase {

    private static Logger log = LogManager.getLogger(TestData.class);

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private DriverFactory driverFactory = new DriverFactory();
    private static String applicationEndPoint ;
    public TestData testData;

    public WebDriver getDriver() {
        return driver.get();
    };

    @SneakyThrows
    public void initialSetup(ParamsEntity entity, String buildId, String dataPath, String capsPath, ITestContext context){

        context.setAttribute("buildId",entity.getBuild());
        String name = context.getCurrentXmlTest().getName();

        log.info("########################################################################################");
        log.info("#########################    Running Tests with following Details.    #########################");
        log.info("#########################    Environment : {}",entity.getEnv());
        log.info("#########################    Setup : {}",entity.getSetup());
        String env = !entity.getEnv().equalsIgnoreCase("dev") && !entity.getEnv().equalsIgnoreCase("stag")  ? "dev" : entity.getEnv();
        testData = new TestData(env,dataPath,capsPath);
        JSONObject testCapsConfig = testData.getTestsCapsConfig();
        String url = (String) testCapsConfig.get("application_endpoint");
        applicationEndPoint = url;
        log.info("#########################    Application End Point is : {}" , url);
        String build = testData.getProperty("website").toUpperCase() + "_" + entity.getSuiteType().toUpperCase() + "_" + env.toUpperCase() + "_" + testData.getBuildId(buildId) ;
        log.info("#########################    Build is : {}" , build);

        Driver driverType = driverFactory.getDriver(entity.getSetup());

        if(driverType instanceof BrowserStackDriver) {
            driver.set(driverType.setupDriver(entity,dataPath,capsPath));
        } else {
            driver.set(driverType.setupDriver(entity));
        }

        getDriver().get(url);
        if (!entity.isMobile()) {
            getDriver().manage().window().maximize();
        }
        context.setAttribute("driver",getDriver());

    }

}
