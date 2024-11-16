package org.example.t100.domain.crawler.service;

import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.crawler.entity.Trend;
import org.example.t100.domain.crawler.repository.TrendRepository;
import org.example.t100.global.Enum.SuccessCode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.example.t100.global.Enum.SuccessCode.*;

@Slf4j
@Service
public class CrawlingService {
    @Autowired
    private TrendRepository trendRepository;
    public SuccessCode crawlAndSave() {
        System.setProperty("webdriver.chrome.driver", "/chromedriver/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);
        driver.get("https://trends.google.co.kr/trends/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='실시간 인기']]")));
        button.click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement pagecount = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/div/div")
        ));

        String tmp = pagecount.getText();
        int allpagecount=Integer.parseInt(tmp.split(" ")[0]);

        for (int i = 1; i <= allpagecount / 25 + 1; i++) {
            pagecount = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/div/div")
            ));
            tmp = pagecount.getText();
            int currentpagecount = 0;
            try {
                String[] pageInfo = tmp.split(" ");
                if (pageInfo.length >= 3 && pageInfo[2].contains("–")) {
                    String[] range = pageInfo[2].split("–");
                    if (range.length == 2) {
                        int start = Integer.parseInt(range[0].trim());
                        int end = Integer.parseInt(range[1].trim());
                        currentpagecount = end - start + 1;
                        log.info("Current page count: {}", currentpagecount);
                    } else {
                        log.error("Unexpected range format: {}", pageInfo[2]);
                    }
                } else {
                    log.error("Unexpected page info format: {}", tmp);
                }
            } catch (NumberFormatException e) {
                log.error("Error parsing current page count: {}", tmp, e);
            }

            for(int j = 1; j <= currentpagecount; j++) {
                WebElement countryElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[1]/div[1]/div/div[1]/div/div[1]/div/button/span[5]")
                ));
                String nation = countryElement.getText().trim();

                WebElement trendElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[" + j + "]/td[2]/div[1]")
                ));
                String keyword = trendElement.getText().trim();

                WebElement volumeElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[" + j + "]/td[3]/div/div[1]")
                ));
                String searchVolume = volumeElement.getText().trim();

                WebElement startElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[@id=\"trend-table\"]/div[1]/table/tbody[2]/tr[" + j + "]/td[4]/div[1]")
                ));
                String starttime = startElement.getText().trim();

                Trend trend = new Trend();
                trend.setKeyword(keyword);
                trend.setSearch_volume(searchVolume);
                trend.setNation(nation);
                //trend.setCategory(category);
                trend.setStart_date(starttime);

                trendRepository.save(trend);
            }
            if(i != allpagecount / 25 + 1) {
                WebElement nextpage = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/span[3]/button")));
                nextpage.click();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        driver.quit();
        return CRAWLING_SUCCESS;
    }
}
