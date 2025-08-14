package com.simba.project01.review;

import lombok.Getter;

@Getter
public class ReviewResponse
{
    private Long id;
    private Long missionId;
    private int rating;
    private String content;
    private String status;
    private Long userId;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.missionId = review.getMission().getId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.status = review.getStatus().name();
        this.userId = review.getUser().getId();
    }
}
