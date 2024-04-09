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

    @Test // 2 pages of ads (but could work on the bigger search)
    public void test2() {
        _globalDriver.get("https://www.skelbiu.lt/skelbimai/1?keywords=samotines+plytos");
        _globalDriver.findElement(By.id("onetrust-reject-all-handler")).click();

        snoozeUntilPresence(By.xpath("//*[@id=\"body-container\"]/div[2]/div[1]/ul/li/span"));
        String totalAds = _globalDriver.findElement(By.xpath("//*[@id=\"body-container\"]/div[2]/div[1]/ul/li/span")).getText();
        String numericPart = totalAds.replaceAll("[^\\d]", "");
        int numberOfAds = Integer.parseInt(numericPart);
        int adCounter = 0;

        for (int i = 1; i < 201; i++) {

            String url = "https://www.skelbiu.lt/skelbimai/" + i + "?keywords=samotines+plytos";
            _globalDriver.get(url);
            if (!_globalDriver.getCurrentUrl().equals(url)) {
                break; // Exit the loop since we've reached the last page
            }

            for (int y = 1; y <= 27; y++) {
                try {
                    _globalDriver.findElement(By.xpath("(//*[@id='items-list-container']/div[" + (i + 1) + "]/div)[" + y + "]")).click();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String adID = _globalDriver.findElement(By.cssSelector("#contentArea > div.main-item-container > div.itemscope > div.left-content > div.actions-container > div.block.id")).getText();
                    if (adID.contains("ID")) {
                        adCounter++;
                        System.out.println(adID);
                    }
                    _globalDriver.get("https://www.skelbiu.lt/skelbimai/" + i + "?keywords=samotines+plytos");
                } catch (NoSuchElementException e) {
                }
            }
        }
        System.out.println("Number of ads: " + adCounter);
        Assert.assertEquals(adCounter, numberOfAds);
        _globalDriver.quit();
    }

    @Test // Search with more than 200 pages
    public void test3() {
        _globalDriver.get("https://www.skelbiu.lt/skelbimai/1?keywords=telefonas");
        _globalDriver.findElement(By.id("onetrust-reject-all-handler")).click();

        snoozeUntilPresence(By.xpath("//*[@id=\"body-container\"]/div[2]/div[1]/ul/li/span"));
        String totalAds = _globalDriver.findElement(By.xpath("//*[@id=\"body-container\"]/div[2]/div[1]/ul/li/span")).getText();
        String numericPart = totalAds.replaceAll("[^\\d]", "");
        int numberOfAds = Integer.parseInt(numericPart);
        int adCounter = 0;

        for (int i = 1; i < 202; i++) {

            String url = "https://www.skelbiu.lt/skelbimai/" + i + "?keywords=telefonas";
            _globalDriver.get(url);
            if (!_globalDriver.getCurrentUrl().equals(url)) {
                break; // Exit the loop since we've reached the last page
            }
            if (_globalDriver.getCurrentUrl().equals("https://www.skelbiu.lt/skelbimai/201?keywords=telefonas")) {
                System.out.println("Pagal Jūsų paieškos kriterijus radome labai daug skelbimų.");
                WebElement limitText = _globalDriver.findElement(By.id("NotFoundHeader"));
                Assert.assertEquals(limitText.getText(), "Pagal Jūsų paieškos kriterijus radome labai daug skelbimų.");
                _globalDriver.quit();
                break;
            }

            for (int y = 1; y <= 27; y++) {
                try {
                    _globalDriver.findElement(By.xpath("(//*[@id='items-list-container']/div[" + (i + 1) + "]/div)[" + y + "]")).click();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String adID = _globalDriver.findElement(By.cssSelector("#contentArea > div.main-item-container > div.itemscope > div.left-content > div.actions-container > div.block.id")).getText();
                    if (adID.contains("ID")) {
                        adCounter++;
                        System.out.println(adID);
                    }
                    _globalDriver.get("https://www.skelbiu.lt/skelbimai/" + i + "?keywords=telefonas");
                } catch (NoSuchElementException e) {
                }
            }
        }
        System.out.println("Number of ads: " + adCounter);
        Assert.assertEquals(adCounter, numberOfAds);
        _globalDriver.quit();
    }

    @Test // search with no results
    public void test4() {
        _globalDriver.get("https://www.skelbiu.lt/skelbimai/1?keywords=drakono+kiausinis&category_id=2");
        _globalDriver.findElement(By.id("onetrust-reject-all-handler")).click();

        WebElement limitText = _globalDriver.findElement(By.id("NotFoundHeader"));
        Assert.assertEquals(limitText.getText(), "Nemalonu pranešti, bet nieko neradome :-(");
        _globalDriver.quit();
    }
}
