package com.dpworld.core.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BasePage {

    private static final Logger log = LogManager.getLogger(BasePage.class);

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver, int time) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, time);
        PageFactory.initElements(driver, this);
    }


    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, 12);
        PageFactory.initElements(driver, this);
    }


    public void waitForPageLoad() {
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver wdriver) {
                return ((JavascriptExecutor) driver).executeScript(
                        "return document.readyState"
                ).equals("complete");
            }
        });

        try {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver wdriver) {
                    ((JavascriptExecutor) driver).executeScript(
                            "return jQuery.active ==0"
                    );
                    return true;
                }
            });
        } catch (Exception e) {
            log.info("This check is not needed ..");
        }
    }


    protected boolean  waitForElementToBePresent(WebElement element) {

        boolean flag = false;
        try {
            wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(element));
            flag = true;
        } catch (Exception e){
            log.error("Element not found . Exception message : {}", e.getMessage() );
        }
        return flag;
    }

    protected boolean  waitForElementToBePresent(List<WebElement> elements) {

        boolean flag = false;
        try {
            wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOfAllElements(elements));
            flag = true;
        } catch (Exception e){
            log.error("Element not found . Exception message : {}", e.getMessage() );
        }
        return flag;
    }

    protected void waitForTextToBePresentInElement(WebElement element, String text) {
        wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    protected void waitAndClick(WebElement element) {
       try {
           waitForVisibilityAndClickable(element);
           element.click();
       } catch (ElementClickInterceptedException e) {
           clickByJavaScript(element);
       }
    }

    protected void waitClick(WebElement element) {
            waitForVisibilityAndClickable(element);
            element.click();
    }

    protected void navigateToUrl(String url) {
            driver.navigate().to(url);
    }

    protected void waitForVisibilityAndClickable(WebElement element) {
        wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions
                .and(ExpectedConditions.visibilityOf(element), ExpectedConditions.elementToBeClickable(element)));
    }


    protected void mouseOver(WebElement element){
        Actions action = new Actions(driver);
        action.moveToElement(element);
        action.perform();
    }

    protected void mouseOverAndClick(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element).click().build().perform();
    }


    protected void mouseOverAndClick(WebElement main, WebElement sub) {
        waitForElementToBePresent(main);
        Actions action = new Actions(driver);
        action.moveToElement(main).moveToElement(sub).click().build().perform();
    }

    protected void clickByJavaScript(WebElement element) {
        try {
            waitForVisibilityAndClickable(element);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            waitClick(element);
        }
    }

    protected void scrollIntoView(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        js.executeScript("window.scrollTo(0,-10)");
    }

    protected void selectElementFromDropDownByText(WebElement element, String txt) {
        waitForElementToBePresent(element);
        Select select = new Select(element);
        select.selectByVisibleText(txt);
    }

    protected void selectElementFromDropDownByIndex(WebElement element, int index) {
        waitForElementToBePresent(element);
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    protected void selectElementFromDropDownByValue(WebElement element, String value) {
        waitForElementToBePresent(element);
        Select select = new Select(element);
        select.selectByValue(value);
    }

    protected void enterDetails(WebElement element,String data) {
        try {
            waitForElementToBePresent(element);
        } catch (Exception e) {
            log.info("Element not present to enter the details . Error message : {} " , e.getMessage());
        }
        clickByJavaScript(element);
        element.clear();
        element.sendKeys(data);
    }


    public void selectFromMultipleWindowsHandle(WebElement element) {
        String mainWindowHandle = driver.getWindowHandle();
        Set<String> allWindowHandles = driver.getWindowHandles();
        Iterator<String> iterator = allWindowHandles.iterator();
        while (iterator.hasNext()) {
            String ChildWindow = iterator.next();
            if (!mainWindowHandle.equalsIgnoreCase(ChildWindow)) {
                driver.switchTo().window(ChildWindow);
                clickByJavaScript(element);
            }
        }
        driver.switchTo().window(mainWindowHandle);
    }

    public String getText(WebElement element) {
        waitForElementToBePresent(element);
        return  element.getText();
    }


    public void acceptAlert() {
        driver.switchTo().alert().accept();
        try {
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            log.info("Moving to default content ..");
        }
    }

    public String getAlertText() {
       return driver.switchTo().alert().getText();
    }

    public void scrollToHalfPage() {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public void scrollToLocationFromTop(int location) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollTo(0,"+location+")");
    }

}
