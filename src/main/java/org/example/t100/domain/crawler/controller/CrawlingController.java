package org.example.t100.domain.crawler.controller;

import lombok.RequiredArgsConstructor;
import org.example.t100.domain.crawler.service.*;
import org.example.t100.global.Enum.SuccessCode;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CrawlingController {

    @Autowired
    private CrawlingService crawlingService;

    @Autowired
    private OtherCrawlingService otherCrawlingService;

    @Autowired
    private BandfCrawlingService bandfCrawlingService;

    @Autowired
    private EnterCrawlingService enterCrawlingService;

    @Autowired
    private LandgCrawlingService landgCrawlingService;

    @Autowired
    private SportsCrawlingService sportsCrawlingService;


    @GetMapping("/crawl")
    public ApiResponse<?> crawl() {
        SuccessCode successCode = crawlingService.crawlAndSave();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/OtherCrawl")
    public ApiResponse<?> OtherCrawl() {
        SuccessCode successCode = otherCrawlingService.crawlAndSaveSpecificCategoryWithCategory();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/BandfCrawl")
    public ApiResponse<?> BandfCrawl() {
        SuccessCode successCode = bandfCrawlingService.crawlAndSaveSpecificCategoryWithCategory();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/EnterCrawl")
    public ApiResponse<?> EnterCrawl() {
        SuccessCode successCode = enterCrawlingService.crawlAndSaveSpecificCategoryWithCategory();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/LandgCrawl")
    public ApiResponse<?> LandgCrawl() {
        SuccessCode successCode = landgCrawlingService.crawlAndSaveSpecificCategoryWithCategory();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/SportsCrawl")
    public ApiResponse<?> SportsCrawl() {
        SuccessCode successCode = sportsCrawlingService.crawlAndSaveSpecificCategoryWithCategory();
        return ResponseUtils.ok(successCode);
    }

}
