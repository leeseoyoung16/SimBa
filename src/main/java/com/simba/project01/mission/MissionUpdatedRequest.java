package com.simba.project01.mission;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MissionUpdatedRequest
{
    @Size(min = 2, max = 100, message = "미션 제목은 2자 이상 100자 이내여야 합니다.")
    private String title;

    @Size(max = 500, message = "미션 설명은 500자 이내여야 합니다.")
    private String description;

    @Future(message = "종료 일시는 현재 이후여야 합니다.")
    private LocalDateTime endAt;

}
