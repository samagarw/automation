package com.dpworld.core.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

public class DriverUtil {

    public static WebDriver getDriver(String browser) {

        switch (BrowserType.valueOf(browser.toUpperCase())) {
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
            case EDGE:
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver();
            case SAFARI:
                WebDriverManager.safaridriver().setup();
                return new SafariDriver();
            default:
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
        }
    }

    public static WebDriver getOnPremDriverDocker(String browser) {

        switch (BrowserType.valueOf(browser.toUpperCase())) {
            case FIREFOX:
                return WebDriverManager.firefoxdriver().browserInDocker().create();
            case EDGE:
                return WebDriverManager.edgedriver().browserInDocker().create();
            case SAFARI:
                return WebDriverManager.safaridriver().browserInDocker().create();
            default:
                return WebDriverManager.chromedriver().browserInDocker().create();
        }
    }
}
