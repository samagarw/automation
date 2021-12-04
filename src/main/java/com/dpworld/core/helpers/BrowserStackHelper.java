package com.dpworld.core.helpers;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.io.File;
import java.util.Iterator;

public class BrowserStackHelper {

    private static final Logger log = LogManager.getLogger(BrowserStackHelper.class);

    /**
     *
     * UpLoad App to BrowserStack .
     *
     * @param username - username for authentication
     * @param accessKey - accesskey for authentication
     * @param url - Browserstack app upload path
     * @param appPath - Path of apk and ipa
     * @return appUrl - The uploaded app path .
     */
    public String uploadAppInDevice(String username, String accessKey, String url, String appPath) {

        Response response = RestAssured.given()
                .multiPart(new File(appPath))
                .auth().preemptive().basic(username, accessKey)
                .contentType("multipart/form-data")
                .log()
                .all()
                .when()
                .post(url);

        int responseCode = response.getStatusCode();

        log.info("Response Body is {} ,  Response Code is {}  " , response.getBody().prettyPrint() , responseCode );

        try {
            Assert.assertTrue(responseCode == 200, "App is not uploaded correctly");
        } catch (Exception e) {
            log.error("App Not Uploaded Correctly");
            return null;
        }

        JsonPath responseBody = response.jsonPath();
        String appUrl  = responseBody.getString("app_url");
        return appUrl;

    }

    public void updateStatus(ITestContext context, WebDriver driver) {
        String failTestCases = "Test Cases Failed are :";
        String passTestCases = "Test Cases Passed are :";
        boolean fail = false;

        Iterator<ITestResult> failedTestCases = context.getFailedTests().getAllResults().iterator();
        while(failedTestCases.hasNext()) {
            fail = true;
            ITestResult failedTestCase = failedTestCases.next();
            ITestNGMethod method = failedTestCase.getMethod();
            failTestCases = failTestCases + "  " + method.getMethodName() + ".";
        }


        Iterator<ITestResult> passedTestCases = context.getPassedTests().getAllResults().iterator();
        while(passedTestCases.hasNext()) {
            ITestResult passedTestCase = passedTestCases.next();
            ITestNGMethod method = passedTestCase.getMethod();
            passTestCases = passTestCases + "  " + method.getMethodName() + ".";
        }
        String mesg = failTestCases + "\n" + passTestCases;

        log.info("Passed Test Cases : {}" ,passTestCases);
        log.info("Failed Test Cases : {}", failTestCases);

        if(fail) {
            markTestStatus("failed", failTestCases , driver);
        } else {
            markTestStatus("passed", "All Tests Passed" , driver);
        }
    }

    private void markTestStatus(String status, String reason, WebDriver driver) {
        try {
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"" + status + "\", \"reason\": \"" + reason + "\"}}");
        } catch (Exception e) {

        }
    }

    public String getSessionId() {
        return null;
    }

    public void updateTestStatus(String username,String accessKey, String status, String mesg) {

        String url = "https://api.browserstack.com/automate/sessions/" + getSessionId() + ".json";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("status",status);
        jsonObject.put("reason",mesg);
        Gson request = new Gson();
        request.toJson(jsonObject);

        Response response = RestAssured.given()
                .auth().preemptive().basic(username, accessKey)
                .contentType(ContentType.JSON)
                .log()
                .all()
                .when()
                .body(request)
                .put(url);

        int responseCode = response.getStatusCode();

        log.info("Response Body is {} ,  Response Code is {}  " , response.getBody().prettyPrint() , responseCode );
    }
}
