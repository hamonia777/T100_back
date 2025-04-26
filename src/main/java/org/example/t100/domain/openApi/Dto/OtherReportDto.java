package org.example.t100.domain.openApi.Dto;

import lombok.Getter;
import lombok.Setter;
import org.example.t100.domain.openApi.entity.OpenApi;
import org.example.t100.domain.openApi.entity.OtherOpenApi;

@Getter
@Setter
public class OtherReportDto {
    public OtherReportDto(OtherOpenApi openApi)
    {
        this.title = openApi.getTitle();
        this.content = openApi.getContent();
    }

    String title;
    String content;
}
