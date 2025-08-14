package com.simba.project01.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewUpdatedRequest
{
    @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5 이하여야 합니다.")
    private Integer rating;

    @Size(min = 10, max = 2000, message = "리뷰 내용은 10자 이상 2000자 이내여야 합니다.")
    private String content;
}
