package com.dpworld.core.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.Iterator;

public class DockerListener extends TestListenerAdapter {

    private static final Logger log = LogManager.getLogger(DockerListener.class);

    @Override
    public void onTestSuccess(ITestResult result) {
        ITestContext context = result.getTestContext();
        WebDriver driver = (WebDriver) context.getAttribute("driver");
        Cookie cookie = new Cookie("zaleniumTestPassed", String.valueOf("true"));
        driver.manage().addCookie(cookie);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ITestContext context = result.getTestContext();
        WebDriver driver = (WebDriver) context.getAttribute("driver");
        Cookie cookie = new Cookie("zaleniumTestPassed", String.valueOf("false"));
        driver.manage().addCookie(cookie);
    }


    @Override
    public void onTestSkipped(ITestResult result) {
        ITestContext context = result.getTestContext();
        WebDriver driver = (WebDriver) context.getAttribute("driver");
        Cookie cookie = new Cookie("zaleniumTestPassed", String.valueOf("false"));
        driver.manage().addCookie(cookie);
    }

    @Override
    public void onFinish(ITestContext context) {
        Iterator<ITestResult> skippedTestCases = context.getSkippedTests().getAllResults().iterator();
        while (skippedTestCases.hasNext()) {
            ITestResult skippedTestCase = skippedTestCases.next();
            ITestNGMethod method = skippedTestCase.getMethod();
            if (context.getSkippedTests().getResults(method).size() > 0) {
                log.info("Removing:" + skippedTestCase.getTestClass().toString());
                skippedTestCases.remove();
            }
        }
    }
}