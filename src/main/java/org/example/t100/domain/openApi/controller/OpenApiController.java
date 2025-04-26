package org.example.t100.domain.openApi.controller;

import lombok.NoArgsConstructor;
import org.example.t100.domain.openApi.Dto.*;
import org.example.t100.domain.openApi.entity.OpenApi;
import org.example.t100.domain.openApi.repository.OpenApiRepository;
import org.example.t100.domain.openApi.service.*;
import org.example.t100.global.Enum.SuccessCode;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@RestController
@NoArgsConstructor
@RequestMapping("/api")
public class OpenApiController {
    @Autowired
    private OpenApiService openApiService;
    @Autowired
    private BandfApiService bandfApiService;
    @Autowired
    private EnterApiService enterApiService;
    @Autowired
    private LandgApiService landgApiService;
    @Autowired
    private OtherApiService otherApiService;
    @Autowired
    private SportsApiService sportsApiService;


    @GetMapping("/chat")
    public ApiResponse<?> call_GPT(){
        SuccessCode successCode = openApiService.create_Report();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/report")
    public ApiResponse<?> get_Report(){
        ReportDto reportDto = openApiService.get_Report();
        return ResponseUtils.ok(reportDto);
    }
    @GetMapping("/Date")
    public String Get_Date(){
        String date=openApiService.get_Date();
        return date;
    }

    @GetMapping("/otherChat")
    public ApiResponse<?> other_Call_GPT(){
        SuccessCode successCode = otherApiService.create_Report();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/otherReport")
    public ApiResponse<?> other_Get_Report(){
        OtherReportDto reportDto = otherApiService.get_Report();
        return ResponseUtils.ok(reportDto);
    }
    @GetMapping("/otherDate")
    public String other_Get_Date(){
        String date=otherApiService.get_Date();
        return date;
    }

    @GetMapping("/bandfChat")
    public ApiResponse<?> bandf_Call_GPT(){
        SuccessCode successCode = bandfApiService.create_Report();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/bandfReport")
    public ApiResponse<?> bandf_Get_Report(){
        BandfReportDto reportDto = bandfApiService.get_Report();
        return ResponseUtils.ok(reportDto);
    }

    @GetMapping("/bandfDate")
    public String bandf_Get_Date(){
        String date=bandfApiService.get_Date();
        return date;
    }

    @GetMapping("/enterChat")
    public ApiResponse<?> enter_Call_GPT(){
        SuccessCode successCode = enterApiService.create_Report();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/enterReport")
    public ApiResponse<?> enter_Get_Report(){
        EnterReportDto reportDto = enterApiService.get_Report();
        return ResponseUtils.ok(reportDto);
    }
    @GetMapping("/enterDate")
    public String enter_Get_Date(){
        String date=enterApiService.get_Date();
        return date;
    }

    @GetMapping("/landgChat")
    public ApiResponse<?> landg_Call_GPT(){
        SuccessCode successCode = landgApiService.create_Report();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/landgReport")
    public ApiResponse<?> landg_Get_Report(){
        LandgReportDto reportDto = landgApiService.get_Report();
        return ResponseUtils.ok(reportDto);
    }
    @GetMapping("/landgDate")
    public String landg_Get_Date(){
        String date=landgApiService.get_Date();
        return date;
    }

    @GetMapping("/sportsChat")
    public ApiResponse<?> sports_Call_GPT(){
        SuccessCode successCode = sportsApiService.create_Report();
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/sportsReport")
    public ApiResponse<?> sports_Get_Report(){
        SportsReportDto reportDto = sportsApiService.get_Report();
        return ResponseUtils.ok(reportDto);
    }
    @GetMapping("/sportsDate")
    public String sports_Get_Date(){
        String date=sportsApiService.get_Date();
        return date;
    }
}
