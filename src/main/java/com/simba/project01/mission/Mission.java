package com.simba.project01.mission;

import com.simba.project01.store.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Mission
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "reward_content", nullable = false, length = 255)
    private String rewardContent;

    @Column(name = "reward_total_count", nullable = false)
    private int rewardTotalCount;

    @Column(nullable = false)
    private int rewardRemainingCount;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    //상태
    @Transient
    public MissionStatus getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startAt)) return MissionStatus.SCHEDULED;
        if (!now.isAfter(endAt))   return MissionStatus.ONGOING;
        return MissionStatus.ENDED;
    }

    //남은 보상 수량
    public void decreaseRewardCount() {
        if (rewardRemainingCount <= 0) {
            throw new IllegalStateException("지급 가능한 보상이 없습니다.");
        }
        rewardRemainingCount--;
    }

    //참여 가능 상태?
    @Transient
    public boolean isJoinable(LocalDateTime when, long approvedReviewCount) {
        LocalDateTime t = (when != null) ? when : LocalDateTime.now();
        boolean inPeriod = !t.isBefore(startAt) && !t.isAfter(endAt);
        boolean hasStock = (rewardTotalCount - approvedReviewCount) > 0;
        return inPeriod && hasStock;
    }

    //기본 검증
    @PrePersist @PreUpdate
    private void validate() {
        if (startAt == null || endAt == null || !startAt.isBefore(endAt)) {
            throw new IllegalArgumentException("미션 기간이 올바르지 않습니다(startAt < endAt).");
        }
        if (rewardTotalCount < 1) {
            throw new IllegalArgumentException("보상 총 수량은 1 이상이어야 합니다.");
        }
    }
}
