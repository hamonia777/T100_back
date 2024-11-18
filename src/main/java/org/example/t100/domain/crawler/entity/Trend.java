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
public class Trend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String keyword;
    String search_volume;
    String nation;
    String category;
    String start_date;
//    @ElementCollection
//    @CollectionTable(name = "trend_news_titles", joinColumns = @JoinColumn(name = "trend_id"))
//    @Column(name = "news_title")
//    List<String> news_title;
}
