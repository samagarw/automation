package com.dpworld.core.factory;

import com.dpworld.core.entities.ParamsEntity;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

import static com.dpworld.core.commons.Constants.DOCKER_SELENIUM_HUB_URL;

public class DockerGridDriver implements Driver {

    private static final Logger log = LogManager.getLogger(DockerGridDriver.class);

    @Override
    public WebDriver setupDriver(ParamsEntity entity, String dataPath, String jsonPath) {
        return null;
    }

    @SneakyThrows
    @Override
    public WebDriver setupDriver(ParamsEntity entity) {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability("zal:name", entity.getBuild());
        dc.setCapability("zal:build", entity.getBuild());
        dc.setCapability("tz","Asia/Kolkata");
        if (entity.getBrowser().equalsIgnoreCase(BrowserType.FIREFOX)) {
            dc.setCapability(CapabilityType.BROWSER_NAME, BrowserType.FIREFOX);
        } else if (entity.getBrowser().equalsIgnoreCase(BrowserType.EDGE)) {
            dc.setCapability(CapabilityType.BROWSER_NAME, BrowserType.EDGE);
        } else {
            dc.setCapability(CapabilityType.BROWSER_NAME, BrowserType.CHROME);
        }
        dc.setCapability(CapabilityType.PLATFORM_NAME, Platform.LINUX);
        dc.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        try {
            return new RemoteWebDriver(new URL(DOCKER_SELENIUM_HUB_URL), dc);
        } catch (Exception e) {
            log.error("Driver was not created . Trying 1 more time ... ");
            return new RemoteWebDriver(new URL(DOCKER_SELENIUM_HUB_URL), dc);
        }
    }
}
