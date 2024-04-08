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
import java.util.Set;

public class SkelbiuTest {
    ChromeDriver _globalDriver;

    public WebElement snoozeUntilPresence(By by) {
        WebDriverWait wait = new WebDriverWait(_globalDriver, Duration.ofSeconds(30));

        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
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
                _globalDriver.findElement(By.xpath("//*[@id=\"items-list-container\"]/div[2]/div[" + i + "]")).click();
                adCounter++;
            } catch (NoSuchElementException e) {
            }
        }
        Assert.assertEquals(adCounter, numberOfAds);

        Set<String> windowHandlesAfterClick = _globalDriver.getWindowHandles(); // Get all window handles after clicking the link
        int autopliusWindowsOpened = windowHandlesAfterClick.size() - 1;
        System.out.println("Autoplius langų: " + autopliusWindowsOpened);

        _globalDriver.quit();
    }

    @Test // 1 page of ads (not skelbiu, but autoplius)
    public void test2() {
        _globalDriver.get("https://www.skelbiu.lt/skelbimai/1?keywords=samotines+plytos");
        _globalDriver.findElement(By.id("onetrust-reject-all-handler")).click();

        snoozeUntilPresence(By.xpath("//*[@id=\"body-container\"]/div[2]/div[1]/ul/li/span"));
        String totalAds = _globalDriver.findElement(By.xpath("//*[@id=\"body-container\"]/div[2]/div[1]/ul/li/span")).getText();
        String numericPart = totalAds.replaceAll("[^\\d]", "");
        int numberOfAds = Integer.parseInt(numericPart);
        int adCounter = 0;

        for (int i = 0; i < 30; i++) {
            try {
                _globalDriver.findElement(By.xpath("//*[@id=\"items-list-container\"]/div[2]/div[" + i + "]")).click();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String adID = _globalDriver.findElement(By.xpath("//*[@id=\"contentArea\"]/div[6]/div[1]/div[1]/div[4]/div[1]")).getText();
                System.out.println(adID);
                if (adID.contains("ID")) {
                    adCounter++;
                    System.out.println(adID);
                }
                _globalDriver.get("https://www.skelbiu.lt/skelbimai/1?keywords=samotines+plytos");
            } catch (NoSuchElementException e) {
            }
        }
        System.out.println("Number of ads: " + adCounter);
    }
}
