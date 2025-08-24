package com.simba.project01.mission;

import com.simba.project01.store.Store;
import com.simba.project01.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService
{
    private final MissionRepository missionRepository;
    private final StoreRepository storeRepository;

    @Value("${file.mission-upload-dir}")
    private String uploadDir;

    //미션 생성
    @Transactional
    public Long create(String title, String description, String rewardContent,
                       int rewardCount, LocalDateTime startAt, LocalDateTime endAt,
                       Long storeId, Long userId, MultipartFile img) throws IOException
    {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다."));
        if (!store.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("이 가게의 소유자만 미션을 생성할 수 있습니다.");
        }

        String imageUrl = null;
        if(img != null && !img.isEmpty()) {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = System.currentTimeMillis() + "_" + img.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            try {
                img.transferTo(filePath.toFile());
                imageUrl = "/upload/" + fileName;
            } catch (IOException e) {
                File f = filePath.toFile();
                if (f.exists()) f.delete();
                throw e;
            }
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
        mission.setImgUrl(imageUrl);
        Mission saved = missionRepository.save(mission);

        return saved.getId();
    }

    //미션 수정
    @Transactional
    public void update(Long missionId, String title, String description,
                       LocalDateTime endAt, Long userId, MultipartFile img) throws IOException
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

        if(img != null && !img.isEmpty()) {
            //기존 파일 삭제
            if (mission.getImgUrl() != null) {
                String oldFileName = Paths.get(mission.getImgUrl()).getFileName().toString();
                File oldFile = new File(uploadDir, oldFileName);
                if (oldFile.exists()) oldFile.delete();
            }

            // 새 파일 저장
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = System.currentTimeMillis() + "_" + img.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            try {
                img.transferTo(filePath.toFile());
                mission.setImgUrl("/upload/" + fileName);
            } catch (IOException e) {
                File f = filePath.toFile();
                if (f.exists()) f.delete();
                throw e;
            }
        }
        missionRepository.save(mission);
    }

    //미션 삭제
    @Transactional
    public void delete(Long missionId, Long userId) throws IOException{
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("미션을 찾을 수 없습니다."));
        if(mission.getRewardRemainingCount() != mission.getRewardTotalCount()) {
            throw new IllegalStateException("이미 참여한 사람이 존재하기 때문에 삭제가 불가능합니다.");
        }
        if (!mission.getStore().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("이 가게의 소유자만 미션을 삭제할 수 있습니다.");
        }

        // 이미지 파일 삭제
        if (mission.getImgUrl() != null) {
            String fileName = Paths.get(mission.getImgUrl()).getFileName().toString();
            File oldFile = new File(uploadDir, fileName);
            if (oldFile.exists()) oldFile.delete();
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