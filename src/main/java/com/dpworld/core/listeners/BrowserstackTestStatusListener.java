package com.dpworld.core.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.Iterator;


public class BrowserstackTestStatusListener extends TestListenerAdapter {

    private static final Logger log = LogManager.getLogger(BrowserstackTestStatusListener.class);


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
        if(!context.getSuite().getXmlSuite().getName().contains(String.valueOf(context.getAttribute("buildId"))))
        context.getSuite().getXmlSuite().setName(context.getSuite().getXmlSuite().getName()+"-"+context.getAttribute("buildId"));
    }
}
