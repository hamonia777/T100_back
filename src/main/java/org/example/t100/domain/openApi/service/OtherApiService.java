package org.example.t100.domain.openApi.service;

import jakarta.persistence.PrePersist;
import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.crawler.entity.OtherTrend;
import org.example.t100.domain.crawler.entity.Trend;
import org.example.t100.domain.crawler.repository.OtherTrendRepository;
import org.example.t100.domain.crawler.repository.TrendRepository;
import org.example.t100.domain.openApi.Dto.ChatGPTRequest;
import org.example.t100.domain.openApi.Dto.ChatGPTResponse;
import org.example.t100.domain.openApi.Dto.OtherReportDto;
import org.example.t100.domain.openApi.Dto.ReportDto;
import org.example.t100.domain.openApi.entity.BandfOpenApi;
import org.example.t100.domain.openApi.entity.OpenApi;
import org.example.t100.domain.openApi.entity.OtherOpenApi;
import org.example.t100.domain.openApi.repository.OpenApiRepository;
import org.example.t100.domain.openApi.repository.OtherApiRepository;
import org.example.t100.global.Enum.SuccessCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.example.t100.global.Enum.SuccessCode.REPORT_CREATE_SUCCESS;

@Service
@Slf4j
public class OtherApiService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @Autowired
    private OtherTrendRepository otherTrendRepository;

    @Autowired
    private OtherApiRepository otherApiRepository;

    public String create_Prompt() {
        // 특정 범위의 트렌드 데이터를 한 번에 조회
        Pageable pageable = PageRequest.of(0, 25, Sort.by("id").descending());
        List<OtherTrend> trends = otherTrendRepository.findAllByOrderByIdDesc(pageable);

        // 위의 코드의 원본
//        List<Trend> trends = trendRepository.findByIdBetween(1L, 100L);
        //StringBuilder promptBuilder = new StringBuilder();

        //promptBuilder.append("키워드들에 대한 카테고리, 나라, 검색량, 시작일, 관련 뉴스 제목입니다. 어떤 일이 이슈인지 분석하고 설명해주세요 이슈 중에서 그 키워드와 연관 키워드가 있다면 그 키워드와 연계해서 분석해주세요\n");

        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("다음은 여러 키워드에 대한 정보입니다.\n");
        promptBuilder.append("각 키워드는 다음 정보를 포함합니다:\n");
        promptBuilder.append("- 키워드\n");
        promptBuilder.append("- 카테고리\n");
        promptBuilder.append("- 국가\n");
        promptBuilder.append("- 검색량\n");
        promptBuilder.append("- 시작일\n");
        promptBuilder.append("- 관련 뉴스 제목 3개\n\n");

        promptBuilder.append("요청사항:\n");
        promptBuilder.append("보고서의 내용을 제외한 다른 불필요한 내용(ex: 물론입니다. , 추가 제안 등등)은 전부 제외하고 보고서의 내용만 넣어 주세요.\n");
        promptBuilder.append("만약 중복된 키워드가 있다면 하나만 사용해 주세요.\n");
        promptBuilder.append("보고서의 내용을 제외한 다른 불필요한 내용(ex: 물론입니다. 등등)은 전부 제외하고 보고서의 내용만 넣어 주세요.\n");
        promptBuilder.append("1. 검색량이 높은 키워드 순서대로 정렬해서 작성해 주세요.\n");
        promptBuilder.append("1-2. 정렬하는 표의 넓이를 모두 같게 통일하고 그 안에 내용만 다르게 해주세요.\n");
        promptBuilder.append("2. 각 키워드는 과하지 않은 선에서 강조되도록 구성해 주세요.\n");
        promptBuilder.append("3. 각 키워드 아래에는 해당 키워드가 왜 주목받고 있는지 설명해 주세요.\n");
        promptBuilder.append("   - 뉴스 제목을 바탕으로 주요 사건이나 이슈를 유추해 주세요.\n");
        promptBuilder.append("   - 키워드와 연관된 다른 키워드가 있다면 함께 설명해 주세요.\n");
        promptBuilder.append("   - 가능하면 해당 이슈의 배경이나 사회적/경제적 함의도 덧붙여 주세요.\n");
        promptBuilder.append("4. 전체 형식은 보기 좋고 깔끔하게, 시각적으로 잘 구분된 섹션 형태로 만들어 주세요.\n");
        promptBuilder.append("5. 너무 짧거나 단편적인 설명 대신, 충분한 정보가 담긴 문단 형태로 분석해 주세요.\n");
        promptBuilder.append("6. 보고서 스타일의 레이아웃을 포함한 포맷으로 구성해 주세요.\n");
        promptBuilder.append("7. 주요 트렌드 분석 표로 표현해 주세요.\n\n");
        promptBuilder.append("보고서의 내용을 제외한 다른 불필요한 내용(ex: 물론입니다. , 추가 제안 등등)은 전부 제외하고 보고서의 내용만 넣어 주세요.\n");

        promptBuilder.append("아래는 키워드 데이터입니다:\n");

        if (trends == null || trends.isEmpty()) {
            return "No trends available.";
        }

        // 트렌드 데이터를 기반으로 프롬프트 생성
        for (OtherTrend trend : trends) {
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

        OtherOpenApi openApi = new OtherOpenApi();
        String title = LocalDate.now().toString() + " 대한민국 분석 보고서";

        String content = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        openApi.setTitle(title);
        openApi.setContent(content);
        openApi.setReportAt(null);

        otherApiRepository.save(openApi);

        return REPORT_CREATE_SUCCESS;
    }

    public OtherReportDto get_Report() {
        OtherOpenApi openApi = otherApiRepository.findFirstByOrderByIdDesc();
        OtherReportDto reportDto= new OtherReportDto(openApi);
        return reportDto;
    }
    public String get_Date() {
        OtherOpenApi openApi = otherApiRepository.findFirstByOrderByIdDesc();
        LocalDateTime reportAt = openApi.getReportAt(); // DB에서 가져온 값
        String dateOnly = reportAt.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        log.info(dateOnly);
        return dateOnly;
    }
}
