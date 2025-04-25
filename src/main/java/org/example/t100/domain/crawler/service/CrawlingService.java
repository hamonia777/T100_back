package org.example.t100.domain.crawler.service;

import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.crawler.entity.Trend;
import org.example.t100.domain.crawler.repository.TrendRepository;
import org.example.t100.global.Enum.SuccessCode;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
        List<Trend> trends = new ArrayList<>();

        try {
            crawlTrend(driver, wait, trends);
            setCategory(driver, wait, trends);
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
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--start-maximized");

        return new ChromeDriver(options);
    }

    private void crawlTrend(WebDriver driver, WebDriverWait wait, List<Trend> trends) {
        //대한민국/지난7일/모든 카테고리/모든 트렌드/검색량 기준
        driver.get("https://trends.google.co.kr/trending?geo=KR&sort=search-volume&hours=168");
        String nation = crawlingNation(wait);
        List<WebElement> keywordElements;//키워드 목록
        List<WebElement> searchVolumeElements; // 검색량 
        List<WebElement> startDateElements;  //시작 날짜

        for (int page = 0; page < 4; page++) {
            keywordElements = crawlingKeyword(driver, wait);
            searchVolumeElements = findElementsSafely(wait, "lqv0Cb", Duration.ofSeconds(10));
            startDateElements = findElementsSafely(wait, "vdw3Ld", Duration.ofSeconds(10));

            try {
                for (int row = 0; row < keywordElements.size(); row++) {
                    log.info("Processing row: {}", row);
                    //각 트렌드의 상세 뉴스 클릭 후 뉴스 제목 수집
                    WebElement clickElement = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[" + (row + 1) + "]/td[2]/div[1]")
                                    //같은 행에 있는 트렌드 분석 키워드의 tr 값은 모두 같아서 row+1로 해놨음.
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

                    Trend trend = new Trend();
                    trend.setKeyword(keyword);
                    trend.setSearchVolume(searchVolume);
                    trend.setStart_date(startDate);
                    trend.setNation(nation);
                    trend.setNewsTitles(newsTitles);
                    trends.add(trend);
                }
            } catch (Exception e) {
                log.error("crawlTrend Error: {}", e.getMessage());
            }
            toNextPage(driver, wait);
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
            return new ArrayList<>(); // 요소가 없으면 빈 리스트 반환
        }
    }
//
//
    private void setCategory(WebDriver driver, WebDriverWait wait, List<Trend> trends) {
        for(int categoryParameter = 1; categoryParameter <= 20; categoryParameter++) {
            log.info("crawlCategory: categoryParameter={}", categoryParameter);
            if (categoryParameter == 12)
                continue;
            driver.get("https://trends.google.co.kr/trending?geo=KR&sort=search-volume&hours=168&category=" + categoryParameter);

            String category = crawlingCategory(wait);
            int totalPageCount = crawlingTotalPageCount(wait);

            for(int page = 1; page <= (totalPageCount + 25) / 25; page++) {
                List<WebElement> keywordElements = crawlingKeyword(driver, wait);
                for (int row = 0; row < keywordElements.size(); row++) {
                    try {
                        String keyword = keywordElements.get(row).getText();
                        for(Trend t : trends){
                            if (t.getKeyword().equals(keyword)) {
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
                if(page != (totalPageCount + 25) / 25) {
                    toNextPage(driver, wait);
                }
            }
        }
    }
//
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
            return category;
        } catch(TimeoutException | NumberFormatException e) {
            log.error("crawlingCategory Error: {}", e.getMessage());
            return null;
        }
    }

    private String crawlingNation(WebDriverWait wait){
        WebElement countryElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[1]/div[1]/div/div[1]/c-wiz/div/div/div[1]/div/button/span[5]")
        ));
        if (countryElement.getText().trim().contains("▾")) {
            return countryElement.getText().trim().split("▾")[0].trim();
        }
        else {
            return countryElement.getText().trim();
        }
    }

    private List<String> crawlingNews(WebDriverWait wait, int row){
        List<String> newsTitles = new ArrayList<>();
        try {
            WebElement clickElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[1]/table/tbody[2]/tr[1]/td[2]/div[1]")
            ));
            clickElement.click();

            List<WebElement> newsTitleElements = findElementsSafely(wait, "QbLC8c", Duration.ofSeconds(10));

            for(int newsTitleRow = 0; newsTitleRow < 3; newsTitleRow++) {
                log.info("{}", newsTitleElements.get(newsTitleRow).getText().trim());
                newsTitles.add(newsTitleElements.get(newsTitleRow).getText().trim());
            }
            //clickElement.click();

            return newsTitles;
        } catch(TimeoutException | NumberFormatException e) {
            log.error("crawlingNews Error: {}", e.getMessage());
            return null;
        }
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
