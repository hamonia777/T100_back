package org.example.t100.domain.crawler.controller;

import org.example.t100.domain.crawler.service.CrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlingController {

    @Autowired
    private CrawlingService crawlingService;

    @GetMapping("/crawl")
    public void crawl() {
        crawlingService.crawlAndSave();
    }
}
