package com.dpworld.core.listeners;

import com.dpworld.core.log.LoggingUtils;
import com.epam.reportportal.message.ReportPortalMessage;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotListener extends TestListenerAdapter {

    public void takeScreenShot(String testName, WebDriver driver,String successMesg) {

        ReportPortalMessage message = null;

        String destDir = System.getProperty("user.dir")  + "/screenshots/";
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");

        new File(destDir).mkdirs();
        String destFile = testName + "_" + dateFormat.format(new Date()) + ".png";

        try {
            FileUtils.copyFile(srcFile, new File(destDir + "/" + destFile));
            String rp_message = testName + "_" + dateFormat;
            message = new ReportPortalMessage(srcFile, rp_message);
            LoggingUtils.logBase64(screenShot,"Test status : " + successMesg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ITestContext context = result.getTestContext();
        Object currentClass = result.getInstance();
        WebDriver driver = (WebDriver) context.getAttribute("driver");
        String testName = currentClass.getClass().getName();
        takeScreenShot(testName,driver,"failed");
    }


    @Override
    public void onTestSuccess(ITestResult result) {
        ITestContext context = result.getTestContext();
        Object currentClass = result.getInstance();
        WebDriver driver = (WebDriver) context.getAttribute("driver");
        String testName = currentClass.getClass().getName();
        takeScreenShot(testName,driver,"passed");
    }


}