package org.example.t100.domain.crawler.controller;

import lombok.RequiredArgsConstructor;
import org.example.t100.domain.crawler.service.CrawlingService;
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

    @GetMapping("/crawl")
    public ApiResponse<?> crawl() {
        SuccessCode successCode = crawlingService.crawlAndSave();
        return ResponseUtils.ok(successCode);
    }
}
