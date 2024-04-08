import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;

public class SkelbiuTest {
    ChromeDriver _globalDriver;

    public WebElement snoozeUntilPresence(By by) {
        WebDriverWait wait = new WebDriverWait(_globalDriver, Duration.ofSeconds(30));

        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement snoozeUntilClickable(By by) {
        WebElement element = snoozeUntilPresence(by);
        WebDriverWait wait = new WebDriverWait(_globalDriver, Duration.ofSeconds(30));

        element = wait.until(ExpectedConditions.elementToBeClickable(by));
        return element;
    }

    @BeforeTest
    public void setupWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        _globalDriver = new ChromeDriver(options);

    }

    @Test // 1 page of ads (not skelbiu, but autoplius)
    public void test1() {
        _globalDriver.get("https://www.skelbiu.lt/skelbimai/?autocompleted=1&keywords=traktorius+22kw&cost_min=&cost_max=&type=0&condition=&cities=0&distance=0&mainCity=0&search=1&category_id=1&user_type=0&ad_since_min=0&ad_since_max=0&visited_page=1&orderBy=-1&detailsSearch=0");
        _globalDriver.findElement(By.id("onetrust-reject-all-handler")).click();

        snoozeUntilPresence(By.xpath("//*[@id=\"body-container\"]/div[2]/div[1]/ul/li[1]/span"));
        String totalAds = _globalDriver.findElement(By.xpath("//*[@id=\"body-container\"]/div[2]/div[1]/ul/li[1]/span")).getText();
        String numericPart = totalAds.replaceAll("[^\\d]", "");
        int numberOfAds = Integer.parseInt(numericPart);

        int adCounter = 0;
        for (int i = 1; i < 10; i++) {
            try {
                _globalDriver.findElement(By.xpath("//*[@id=\"items-list-container\"]/div[2]/div[" + i + "]"));
                adCounter++;
            } catch (NoSuchElementException e) {
            }
        }
        Assert.assertEquals(adCounter, numberOfAds);

        _globalDriver.quit();
    }

}
