package org.example.t100.domain.crawler.service;

import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.crawler.entity.Trend;
import org.example.t100.domain.crawler.repository.TrendRepository;
import org.example.t100.global.Enum.SuccessCode;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));;

        driver.get("https://trends.google.co.kr/trending?geo=KR&sort=search-volume&hours=168");

        List<Trend> trend = new ArrayList<>();

        int allpagecount;
        WebElement pagecount;
        pagecount = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/div/div")
        ));
        String tmp = pagecount.getText().trim();
        allpagecount = Integer.parseInt(tmp.split(" ")[0]);
        log.info("Current page count: {}", allpagecount);

        for (int j = 1; j <= allpagecount / 25 + 1; j++) {
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

            for (int k = 1; k <= currentpagecount; k++) {
                WebElement countryElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[1]/div[1]/div/div[1]/div/div[1]/div/button/span[5]")
                ));
                String nation = countryElement.getText().trim();
                if (nation.contains("▾")) {
                    nation = nation.split("▾")[0].trim();
                }

                WebElement trendElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[" + k + "]/td[2]/div[1]")
                ));
                String keyword = trendElement.getText().trim();

                WebElement volumeElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[" + k + "]/td[3]/div/div[1]")
                ));
                String searchVolume = volumeElement.getText().trim();

                WebElement startElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[@id=\"trend-table\"]/div[1]/table/tbody[2]/tr[" + k + "]/td[4]/div[1]")
                ));
                String starttime = startElement.getText().trim();

                Trend trendtmp = new Trend();
                trendtmp.setKeyword(keyword);
                trendtmp.setSearch_volume(searchVolume);
                trendtmp.setNation(nation);
                trendtmp.setStart_date(starttime);
                trend.add(trendtmp);
            }
            if (j != allpagecount / 25 + 1) {
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

        for(int i = 1; i <= 20; i++) {
            if (i == 12)
                continue;
            driver.get("https://trends.google.co.kr/trending?geo=KR&sort=search-volume&hours=168&category=" + i);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            WebElement categoryElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[1]/div[1]/div/div[3]/div/div[1]/div/button/span[5]")
            ));
            String category = categoryElement.getText().trim();
            if(category.contains("▾")){
                category = category.split("▾")[0].trim();
            }

            pagecount = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/div/div")
            ));
            tmp = pagecount.getText().trim();
            // 요소가 발견되었을 경우 처리
            if(tmp.isEmpty()){
                log.warn("Category {}: Page count element not found, skipping...", i);
                continue;
            }
            allpagecount = Integer.parseInt(tmp.split(" ")[0]);
            log.info("Category {}: Total pages: {}", i, allpagecount);

            for (int j = 1; j <= allpagecount / 25 + 1; j++) {
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

                for (int k = 1; k <= currentpagecount; k++) {
                    WebElement trendElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[" + k + "]/td[2]/div[1]")
                    ));
                    String keyword = trendElement.getText().trim();

                    for (Trend t : trend) {
                        if (t.getKeyword().equals(keyword)) {
                            if(t.getCategory() == null){
                                t.setCategory(category);
                                break;
                            }
                        }
                    }

                    trendRepository.saveAll(trend);
                }
                if (j != allpagecount / 25 + 1) {
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
        }

        driver.quit();
        return CRAWLING_SUCCESS;
    }
}
