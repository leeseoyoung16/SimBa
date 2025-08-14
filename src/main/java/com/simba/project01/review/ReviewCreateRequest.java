package com.simba.project01.review;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ReviewCreateRequest
{
    @NotNull(message = "평점은 필수 입력 항목입니다.")
    @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5 이하여야 합니다.")
    private Integer rating;

    @NotBlank(message = "리뷰 내용은 필수 입력 항목입니다.")
    @Size(min = 10, max = 2000, message = "리뷰 내용은 10자 이상 2000자 이내여야 합니다.")
    private String content;

}
