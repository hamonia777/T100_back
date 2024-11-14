package org.example.t100.domain.crawler.service;

import org.example.t100.domain.crawler.entity.Trend;
import org.example.t100.domain.crawler.repository.TrendRepository;
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

@Service
public class CrawlingService {
    @Autowired
    private TrendRepository trendRepository;
    public void crawlAndSave() {
        System.setProperty("webdriver.chrome.driver", "/chromedriver/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);
        driver.get("https://trends.google.co.kr/trending?geo=KR&hl=ko&hours=168");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".AeBiU-LgbsSe")));

        // "대한민국" 텍스트를 추출하는 코드
        WebElement nationButton = driver.findElement(By.cssSelector(".AeBiU-LgbsSe"));
        String nation = nationButton.getText().replace("location_on", "").split("▾")[0].trim();
        Trend trend = new Trend();
        //trend.setKeyword(keyword);
        //trend.setSearchVolume(searchVolume);
        trend.setNation(nation);
        //trend.setCategory(category);
        //trend.setStartDate(LocalDate.now());

        trendRepository.save(trend);
        driver.quit();
    }
}
