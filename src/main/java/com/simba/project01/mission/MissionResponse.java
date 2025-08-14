package com.simba.project01.mission;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MissionResponse
{
    private Long id;

    private String title;

    private String description;

    private String rewardContent;

    private int rewardTotalCount;

    private int rewardRemainingCount;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Long storeId;

    public MissionResponse(Mission mission) {
        this.id = mission.getId();
        this.title = mission.getTitle();
        this.description = mission.getDescription();
        this.rewardContent = mission.getRewardContent();
        this.rewardTotalCount = mission.getRewardTotalCount();
        this.rewardRemainingCount = mission.getRewardRemainingCount();
        this.startAt = mission.getStartAt();
        this.endAt = mission.getEndAt();
        this.storeId = mission.getStore().getId();
    }
}
