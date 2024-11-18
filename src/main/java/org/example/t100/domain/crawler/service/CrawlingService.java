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
        WebDriver driver = initWebdriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        ;
        List<Trend> trends = new ArrayList<>();

        try {
            crawlKeyword(driver, wait, trends);
            crawlCategory(driver, wait, trends);

            saveTrends(trends);
        } catch (Exception e) {
            log.error("crawlAndSave Error: {}", e.getMessage());
        } finally {
            driver.quit();
        }

        return CRAWLING_SUCCESS;

    }

    private WebDriver initWebdriver() {
        System.setProperty("webdriver.chrome.driver", "/chromedriver/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

        return new ChromeDriver(options);
    }

    private void crawlKeyword(WebDriver driver, WebDriverWait wait, List<Trend> trends) {
        driver.get("https://trends.google.co.kr/trending?geo=KR&sort=search-volume&hours=168");

        int totalPageCount = crawlingTotalPageCount(wait);
        for(int page = 1; page <= totalPageCount / 25 + 1; page++) {
            crawling(driver, wait, trends);

            if(page != totalPageCount / 25 + 1) {
                toNextPage(driver, wait);
            }
        }
    }

    private void crawlCategory(WebDriver driver, WebDriverWait wait, List<Trend> trends) {
        for(int categoryParameter = 1; categoryParameter <= 20; categoryParameter++) {
            log.info("crawlCategory: categoryParameter={}", categoryParameter);
            if (categoryParameter == 12)
                continue;
            driver.get("https://trends.google.co.kr/trending?geo=KR&sort=search-volume&hours=168&category=" + categoryParameter);

            String category = crawlingCategory(wait);
            int totalPageCount = crawlingTotalPageCount(wait);

            for(int page = 1; page <= totalPageCount / 25 + 1; page++) {
                setCategory(driver, wait, trends, category);

                if(page != totalPageCount / 25 + 1) {
                    toNextPage(driver, wait);
                }
            }
        }
    }

    private int crawlingTotalPageCount(WebDriverWait wait) {
        try {
            WebElement pageInfoElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/div/div")
            ));

            String totalPagecount = pageInfoElement.getText().trim();
            return Integer.parseInt(totalPagecount.split(" ")[0]);
        } catch(TimeoutException | NumberFormatException e) {
            log.error("crawlingTotalPageCount Error: {}", e.getMessage());
            return 0;
        }
    }

    private String crawlingCategory(WebDriverWait wait) {
        try {
            WebElement categoryElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[1]/div[1]/div/div[3]/div/div[1]/div/button/span[5]")
            ));

            String category = categoryElement.getText().trim();
            if(category.contains("▾")){
                category = category.split("▾")[0].trim();
            }
            return category;
        } catch(TimeoutException | NumberFormatException e) {
            log.error("crawlingCategory Error: {}", e.getMessage());
            return null;
        }
    }

    private void setCategory(WebDriver driver, WebDriverWait wait, List<Trend> trends, String category) {
        int totalRowCount = crawlingTotalRowCount(wait);
        for(int row = 1; row <= totalRowCount; row++ ) {
            try {
                Trend trend = crawlingTrend(wait, row);
                for(Trend t : trends){
                    if (t.getKeyword().equals(trend.getKeyword())) {
                        if(t.getCategory() == null){
                            t.setCategory(category);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("crawling Error: {}", e.getMessage());
            }
        }
    }

    private void crawling(WebDriver driver, WebDriverWait wait, List<Trend> trends) {
        int totalRowCount = crawlingTotalRowCount(wait);
        for(int row = 1; row <= totalRowCount; row++ ) {
            try {
                Trend trend = crawlingTrend(wait, row);
                trends.add(trend);
            } catch (Exception e) {
                log.error("crawling Error: {}", e.getMessage());
            }
        }
    }

    private int crawlingTotalRowCount(WebDriverWait wait) {
        try {
            WebElement pageInfoElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/div/div")
            ));

            String[] pageInfo = pageInfoElement.getText().trim().split(" ");
            if (pageInfo.length >= 3 && pageInfo[2].contains("–")) {
                String[] range = pageInfo[2].split("–");
                if (range.length == 2) {
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    log.info("Page Total Row Count: {}", end - start + 1);
                    return end - start + 1;
                } else {
                    log.error("Unexpected range format: {}", pageInfo[2]);
                }
            } else {
                log.error("Unexpected page info format: {}", pageInfoElement.getText().trim());
            }
        } catch(TimeoutException | NumberFormatException e) {
            log.error("crawlingTotalPageCount Error: {}", e.getMessage());
        }
        return 0;
    }

    private Trend crawlingTrend(WebDriverWait wait, int row) {
        Trend trend = new Trend();

        WebElement countryElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[1]/div[1]/div/div[1]/div/div[1]/div/button/span[5]")
        ));
        if (countryElement.getText().trim().contains("▾")) {
            trend.setNation(countryElement.getText().trim().split("▾")[0].trim());
        }
        else {
            trend.setNation(countryElement.getText().trim());
        }

        WebElement trendElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[" + row + "]/td[2]/div[1]")
        ));
        trend.setKeyword(trendElement.getText().trim());

        WebElement volumeElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[" + row + "]/td[3]/div/div[1]")
        ));
        trend.setSearch_volume(volumeElement.getText().trim());

        WebElement startElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@id=\"trend-table\"]/div[1]/table/tbody[2]/tr[" + row + "]/td[4]/div[1]")
        ));
        trend.setStart_date(startElement.getText().trim());

        return trend;
    }

    private void toNextPage(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement nextpage = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/span[3]/button")));
            nextpage.click();
        } catch(TimeoutException | NumberFormatException e) {
            log.error("toNextPage Error: {}", e.getMessage());
        }
    }

    private void saveTrends(List<Trend> trends) {
        trendRepository.saveAll(trends);
    }
}
