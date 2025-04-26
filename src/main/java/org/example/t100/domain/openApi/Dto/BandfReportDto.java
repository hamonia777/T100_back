package org.example.t100.domain.openApi.Dto;

import lombok.Getter;
import lombok.Setter;
import org.example.t100.domain.openApi.entity.BandfOpenApi;
import org.example.t100.domain.openApi.entity.OpenApi;

@Getter
@Setter
public class BandfReportDto {
    public BandfReportDto(BandfOpenApi openApi)
    {
        this.title = openApi.getTitle();
        this.content = openApi.getContent();
    }

    String title;
    String content;
}
