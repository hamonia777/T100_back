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

//
//    selenium webdriver로 특정 클래스명을 가진 요소들을 안전하게 찾는 크롤링 코드
//            mZ3RIc 라는 클래스를 가진 요소들을 wait을 서서 일정 시간 기다리며 찾는 구조
//            실패 시에도 예외 안 터지게 빈 ArrayList<>()를 반환
//            Thread.sleep(1000)
//                    → 크롤링 타이밍 맞추기 위해 1초 대기
//                    → 페이지 로딩 또는 애니메이션 방지용으로 자주 씀
//
//            wait.withTimeout(timeout).until(...)
//                → 최대 timeout 시간 동안 요소가 나타나기를 기다림
//
//            .ignoring(NoSuchElementException.class)
//                → 해당 클래스가 없더라도 예외 안 터지고 계속 기다리게 함
//
//                실패하면 TimeoutException을 잡고 빈 리스트 반환
//        이 코드는 selenium 크롤링에서 요소를 안정적으로  찾기 위한 유틸 함수
//            findElementsSafely()는 페이지 로딩 지연이나 요소 누락 상황에서도 크롤링이 중단되지 않도록 해주는 역할
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

//    카테고리 번호 1~20번까지 반복하면서, 각 카테고리의 트렌드 키워드 페이지를 방문
//    페이지수 (totalPageCount)만큼 반복하면서, 키워드 리스트를 크롤링, 해당 키워드가 기존 trends 리스트에 있으면 -> 카테고리를 설정
//    페이지 이동
//    crawlingCategory(wait) - 현재 페이지에서 카테고리 이름을 추출함
//    crawlingTotalPageCount(wait) - 총 몇 개의 키워드가 있는지 파악해서 -> 페이지 수 계산 (25개당 한 페이지)
//    List<webElement> KeywordElements = crawlingkeyword(driver,wait);- 각 페이지의 키워드 요소들을 찾아서 리스트로 반환
//    이때 사용하는 게 앞에서 말한 findElementsSafely()
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

//    현재 카테고리 페이지에서 총 키워드 개수를 가져와서, 페이지 수 계산을 위한 숫자를 리턴함
//            해당 xpath 위치에 있는 div에서 페이지 정보 문자열 가져옴
//    공백 기준으로 자르고 첫 번째 숫자만 사용-> 정수 변환
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

//    카테고리 이름을 가져오는 메서드
//            드롭다운 버튼 안의 span[5]에서 텍스트를 추출
//
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

//    선택된 국가 이름을 가져옴
//            드롭다운에 선택된 나라 이름이 들어 있음
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

//    트렌드 키워드 중 n번째(row) 키워드를 클릭해서,관련 뉴스 제목 3개를 가져옴
//            클릭 후 뜨는 뉴스 목록 중 qbLC8c 클래스를 가진 요소들을 찾아 옴
//            최대 3개의 뉴스 타이틀을 리스트에 추가
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
    //다음 페이지로 넘기는?
    private void toNextPage(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement nextpage = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/c-wiz/div/div[5]/div[1]/c-wiz/div/div[2]/div[1]/div[1]/div[2]/div/div[2]/span[3]/button")));
            nextpage.click();
        } catch(TimeoutException | NumberFormatException e) {
            log.error("toNextPage Error: {}", e.getMessage());
        }
    }
    //저장
    private void saveTrends(List<Trend> trends) {
        trendRepository.saveAll(trends);
    }
}
