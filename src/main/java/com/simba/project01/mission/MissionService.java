package com.simba.project01.mission;

import com.simba.project01.store.Store;
import com.simba.project01.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService
{
    private final MissionRepository missionRepository;
    private final StoreRepository storeRepository;

    //미션 생성
    @Transactional
    public Long create(String title, String description, String rewardContent,
                       int rewardCount, LocalDateTime startAt, LocalDateTime endAt, Long storeId, Long userId)
    {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다."));
        if (!store.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("이 가게의 소유자만 미션을 생성할 수 있습니다.");
        }
        Mission mission = new Mission();
        mission.setTitle(title);
        mission.setDescription(description);
        mission.setRewardContent(rewardContent);
        mission.setRewardTotalCount(rewardCount);
        mission.setRewardRemainingCount(rewardCount);
        mission.setStartAt(startAt);
        mission.setEndAt(endAt);
        mission.setStore(store);
        Mission saved = missionRepository.save(mission);

        return saved.getId();
    }

    //미션 수정
    @Transactional
    public void update(Long missionId, String title, String description, LocalDateTime endAt, Long userId)
    {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("미션을 찾을 수 없습니다."));

        Store store = mission.getStore();
        if (!store.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("이 가게의 소유자만 미션을 수정할 수 있습니다.");
        }

        if (mission.getStatus() == MissionStatus.ENDED) {
            throw new IllegalStateException("이미 종료된 미션은 수정할 수 없습니다.");
        }

        if (title != null) {
            mission.setTitle(title);
        }

        if (description != null) {
            mission.setDescription(description);
        }

        if (endAt != null) {
            if (!mission.getStartAt().isBefore(endAt)) {
                throw new IllegalArgumentException("종료 시각은 시작 시각보다 나중이어야 합니다.");
            }
            if (endAt.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("종료 시각을 과거로 설정할 수 없습니다.");
            }
            mission.setEndAt(endAt);
        }
        missionRepository.save(mission);
    }

    //미션 삭제
    @Transactional
    public void delete(Long missionId, Long userId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("미션을 찾을 수 없습니다."));
        if(mission.getRewardRemainingCount() != mission.getRewardTotalCount()) {
            throw new IllegalStateException("이미 참여한 사람이 존재하기 때문에 삭제가 불가능합니다.");
        }
        if (!mission.getStore().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("이 가게의 소유자만 미션을 삭제할 수 있습니다.");
        }
        missionRepository.delete(mission);
    }

    //미션 단건
    @Transactional(readOnly = true)
    public Mission getOne(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("미션을 찾을 수 없습니다."));
    }

    //가게 별 미션
    @Transactional(readOnly = true)
    public List<Mission> getByStore(Long storeId) {
        return missionRepository.findByStoreIdOrderByStartAtDesc(storeId);
    }

    // 진행중 + 재고 남음(승인 리뷰 기반)만
    @Transactional(readOnly = true)
    public List<Mission> getJoinable(LocalDateTime now) {
        return missionRepository.findJoinable(now != null ? now : LocalDateTime.now());
    }

    // 상태별(기간 기반)
    @Transactional(readOnly = true)
    public List<Mission> getByStatus(MissionStatus status, LocalDateTime now) {
        LocalDateTime t = now != null ? now : LocalDateTime.now();
        return switch (status) {
            case SCHEDULED -> missionRepository.findScheduled(t);
            case ONGOING   -> missionRepository.findOngoing(t);
            case ENDED     -> missionRepository.findEnded(t);
        };
    }
}