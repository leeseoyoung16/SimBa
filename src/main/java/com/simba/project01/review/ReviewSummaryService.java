package com.simba.project01.review;

import com.simba.project01.summary.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewSummaryService
{
    private final ReviewRepository reviewRepository;
    private final SummaryService summaryService;

    public String summarizeReviews(Long storeId) {
        List<Review> reviews = reviewRepository.findTop500ByMission_Store_IdOrderByCreatedAtDesc(storeId);
        if(reviews.isEmpty()) {
            return "리뷰가 하나도 없습니다.";
        }

        String allReivews = reviews.stream()
                .map(Review::getContent)
                .collect(Collectors.joining("\n"));

        return summaryService.summarize(allReivews);
    }
}
