package com.dpworld.core.clients;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.ThreadLocalRandom;

public class BaseClient {

    protected WebDriver driver;

    public BaseClient(WebDriver driver) {
        this.driver  = driver;
    }

    public int getRandomNumber() {
        int randomNumber= Math.abs(ThreadLocalRandom.current().nextInt());
        return randomNumber;
    }

}
