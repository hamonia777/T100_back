package org.example.t100.domain.openApi.service;

import jakarta.persistence.PrePersist;
import org.example.t100.domain.crawler.entity.Trend;
import org.example.t100.domain.crawler.repository.TrendRepository;
import org.example.t100.domain.openApi.Dto.ChatGPTRequest;
import org.example.t100.domain.openApi.Dto.ChatGPTResponse;
import org.example.t100.domain.openApi.Dto.ReportDto;
import org.example.t100.domain.openApi.entity.BandfOpenApi;
import org.example.t100.domain.openApi.entity.OpenApi;
import org.example.t100.domain.openApi.repository.OpenApiRepository;
import org.example.t100.global.Enum.SuccessCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.example.t100.global.Enum.SuccessCode.REPORT_CREATE_SUCCESS;

@Slf4j
@Service
public class OpenApiService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @Autowired
    private TrendRepository trendRepository;

    @Autowired
    private OpenApiRepository openApiRepository;

    public String create_Prompt() {
        // 특정 범위의 트렌드 데이터를 한 번에 조회
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id").descending());
        List<Trend> trends = trendRepository.findAllByOrderByIdDesc(pageable);

        // 위의 코드의 원본
//        List<Trend> trends = trendRepository.findByIdBetween(1L, 100L);
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("키워드들에 대한 카테고리, 나라, 검색량, 시작일, 관련 뉴스 제목입니다. 어떤 일이 이슈인지 분석하고 설명해주세요 이슈 중에서 그 키워드와 연관 키워드가 있다면 그 키워드와 연계해서 분석해주세요.그리고 다른 사족(ex:어떤 내용을 더 추가 해드릴까요? 같은 말)은 전부 빼고 보고서의 내용만 보고서로 만들어 주세요.\n");

        if (trends == null || trends.isEmpty()) {
            return "No trends available.";
        }

        // 트렌드 데이터를 기반으로 프롬프트 생성
        for (Trend trend : trends) {
            String keywordInfo = "";
            keywordInfo += trend.getKeyword() + ", " + trend.getCategory() + ", " + trend.getNation() + ", " + trend.getSearchVolume() + ", " + trend.getStart_date();
            try {
                keywordInfo += ", " + trend.getNewsTitles().get(0) + ", " + trend.getNewsTitles().get(1) + ", " + trend.getNewsTitles().get(2);
            }catch(Exception e){
                log.error("news is empty: {}", e.getMessage());
            }
            log.info(keywordInfo);
            promptBuilder.append(keywordInfo).append("\n");
        }

        return promptBuilder.toString();
    }

    @PrePersist
    public SuccessCode create_Report() {
        String prompt = create_Prompt();

        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGPTResponse chatGPTResponse =  template.postForObject(apiURL, request, ChatGPTResponse.class);

        OpenApi openApi = new OpenApi();
        String title = LocalDate.now().toString() + " 대한민국 분석 보고서";

        String content = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        openApi.setTitle(title);
        openApi.setContent(content);
        openApi.setReportAt(null);

        openApiRepository.save(openApi);

        return REPORT_CREATE_SUCCESS;
    }

    public ReportDto get_Report() {
        OpenApi openApi = openApiRepository.findFirstByOrderByIdDesc();
        ReportDto reportDto= new ReportDto(openApi);
        return reportDto;
    }

    public String get_Date() {
        OpenApi openApi = openApiRepository.findFirstByOrderByIdDesc();
        LocalDateTime reportAt = openApi.getReportAt(); // DB에서 가져온 값
        String dateOnly = reportAt.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        log.info(dateOnly);
        return dateOnly;
    }
}

