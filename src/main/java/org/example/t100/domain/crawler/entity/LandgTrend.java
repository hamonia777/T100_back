package org.example.t100.domain.crawler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class LandgTrend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String keyword;
    String searchVolume;
    String nation;
    String category;
    String start_date;

    @ElementCollection
    @CollectionTable(name = "landg_trend_news_titles", joinColumns = @JoinColumn(name = "LandgTrend_id"))
    @Column(name = "news_title")
    List<String> newsTitles;
}
