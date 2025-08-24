package com.simba.project01.mission;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MissionCreateRequest
{
    private Long storeId;

    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(min = 2, max = 100, message = "미션 제목은 2자 이상 100자 이내여야 합니다.")
    private String title;

    @Size(max = 500, message = "미션 설명은 500자 이내여야 합니다.")
    private String description;

    @NotBlank(message = "보상 내용은 필수 항목입니다.")
    @Size(min = 2, max = 100, message = "보상 내용은 2자 이상 100자 이내여야 합니다.")
    private String rewardContent;

    @NotNull(message = "보상 개수는 필수 항목입니다.")
    @Min(value = 1, message = "보상 개수는 1개 이상이어야 합니다.")
    @Max(value = 500, message = "보상 개수는 500개 이하여야 합니다.")
    private Integer rewardCount;

    @NotNull(message = "시작 일시는 필수 항목입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @FutureOrPresent(message = "시작 일시는 현재보다 과거일 수 없습니다.")
    private LocalDateTime startAt;

    @NotNull(message = "종료 일시는 필수 항목입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Future(message = "종료 일시는 현재 이후여야 합니다.")
    private LocalDateTime endAt;

    //startAt < endAt 검증
    @AssertTrue(message = "종료 일시는 시작 일시보다 나중이어야 합니다.")
    public boolean isPeriodValid() {
        if (startAt == null || endAt == null) return true;
        return startAt.isBefore(endAt);
    }
}
