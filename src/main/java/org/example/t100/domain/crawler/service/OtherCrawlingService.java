package org.example.t100.domain.crawler.service;


import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.crawler.entity.OtherTrend;
import org.example.t100.domain.crawler.entity.Trend;
import org.example.t100.domain.crawler.repository.OtherTrendRepository;
import org.example.t100.domain.crawler.repository.TrendRepository;
//import org.example.t100.domain.crawler.repository.TrendRepository;
import org.example.t100.global.Enum.SuccessCode;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.MediaSize;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.example.t100.global.Enum.SuccessCode.*;

@Service
@Slf4j
public class OtherCrawlingService {
    @Autowired
    private OtherTrendRepository otherTrendRepository;

//    @Value("${11}")
//    private Integer crawlingCategoryParam;

    public SuccessCode crawlAndSaveSpecificCategoryWithCategory() {
        WebDriver driver = initWebdriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<OtherTrend> trends = new ArrayList<>();

        try {
            crawlSpecificCategoryWithCategory(driver, wait, trends, 11);
            saveTrends(trends);
        } catch (Exception e) {
            log.error("crawlAndSaveSpecificCategoryWithCategory Error: {}", e.getMessage());
        } finally {
            driver.quit();
        }

        return CRAWLING_SUCCESS;
    }

    private WebDriver initWebdriver() {
        System.setProperty("webdriver.chrome.driver", "/chromedriver/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--start-maximized");

        return new ChromeDriver(options);
    }

    private void crawlSpecificCategoryWithCategory(WebDriver driver, WebDriverWait wait, List<OtherTrend> trends, int categoryParam) {
        if (categoryParam == 12) {
            log.warn("선택한 카테고리(12)는 크롤링 대상에서 제외됩니다.");
            return;
        }

        driver.get("https://trends.google.co.kr/trending?geo=KR&hours=168&sort=search-volume&category=11");
        String nation = crawlingNation(wait);
        String category = crawlingCategory(wait); // 현재 페이지의 카테고리 이름을 가져옵니다.
        int totalPageCount = crawlingTotalPageCount(wait);

        for (int page = 1; page <= 1; page++) {
            List<WebElement> keywordElements = crawlingKeyword(driver, wait);
            List<WebElement> searchVolumeElements = findElementsSafely(wait, "lqv0Cb", Duration.ofSeconds(10));
            List<WebElement> startDateElements = findElementsSafely(wait, "vdw3Ld", Duration.ofSeconds(10));

            try {
                for (int row = 0; row < keywordElements.size(); row++) {
                    log.info("Processing row: {}", row);
                    WebElement clickElement = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[" + (row + 1) + "]/td[2]/div[1]")
                    ));
                    clickElement.click();

                    List<WebElement> newsTitleElements = findElementsSafely(wait, "QbLC8c", Duration.ofSeconds(10));
                    List<String> newsTitles = new ArrayList<>();

                    for (int newsTitleRow = 0; newsTitleRow < Math.min(3, newsTitleElements.size()); newsTitleRow++) {
                        String newsTitle = newsTitleElements.get(newsTitleRow).getText().trim();
                        log.info("News title (row {}): {}", newsTitleRow, newsTitle);
                        newsTitles.add(newsTitle);
                    }

                    String keyword = keywordElements.get(row).getText();
                    String searchVolume = searchVolumeElements.get(row).getText();
                    String startDate = startDateElements.get(row).getText();

                    OtherTrend trend = new OtherTrend();
                    trend.setKeyword(keyword);
                    trend.setSearchVolume(searchVolume);
                    trend.setStart_date(startDate);
                    trend.setNation(nation);
                    trend.setCategory(category); // 크롤링한 카테고리 이름을 바로 설정합니다.
                    trend.setNewsTitles(newsTitles);
                    trends.add(trend);
                }
            } catch (Exception e) {
                log.error("crawlSpecificCategoryWithCategory Inner Loop Error: {}", e.getMessage());
            }
            if (page != (totalPageCount + 25) / 25) {
                toNextPage(driver, wait);
            }
        }
    }

    private List<WebElement> crawlingKeyword(WebDriver driver, WebDriverWait wait) {
        List<WebElement> keywordElements = findElementsSafely(wait, "mZ3RIc", Duration.ofSeconds(10));
        return keywordElements;
    }

    private List<WebElement> findElementsSafely(WebDriverWait wait, String className, Duration timeout) {
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return wait.withTimeout(timeout)
                    .ignoring(NoSuchElementException.class)
                    .until(driver -> driver.findElements(By.className(className)));
        } catch (TimeoutException e) {
            return new ArrayList<>();
        }
    }

    private int crawlingTotalPageCount(WebDriverWait wait) {
        try {
            WebElement pageInfoElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/div/div")
            ));
            String totalPagecount = pageInfoElement.getText().trim();
            return Integer.parseInt(totalPagecount.split(" ")[0]);
        } catch (TimeoutException | NumberFormatException e) {
            log.error("crawlingTotalPageCount Error: {}", e.getMessage());
            return 0;
        }
    }

    private String crawlingCategory(WebDriverWait wait) {
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            WebElement categoryElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[1]/div[1]/div/div[3]/div/div[1]/div/button/span[5]")
            ));
            String category = categoryElement.getText().trim();
            if (category.contains("▾")) {
                category = category.split("▾")[0].trim();
            }
            return category;
        } catch (TimeoutException | NumberFormatException e) {
            log.error("crawlingCategory Error: {}", e.getMessage());
            return null;
        }
    }

    private String crawlingNation(WebDriverWait wait) {
        WebElement countryElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[1]/div[1]/div/div[1]/c-wiz/div/div/div[1]/div/button/span[5]")
        ));
        if (countryElement.getText().trim().contains("▾")) {
            return countryElement.getText().trim().split("▾")[0].trim();
        } else {
            return countryElement.getText().trim();
        }
    }

    private void toNextPage(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement nextpage = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/span[3]/button")));
            nextpage.click();
        } catch (TimeoutException | NumberFormatException e) {
            log.error("toNextPage Error: {}", e.getMessage());
        }
    }

    private void saveTrends(List<OtherTrend> trends) {
        otherTrendRepository.saveAll(trends);
    }
}
