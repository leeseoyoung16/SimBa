package com.simba.project01.mission;

import com.simba.project01.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/missions")
public class MissionController
{
    private final MissionService missionService;
    //미션 생성
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody MissionCreateRequest request, @AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getId();
        Long missionId = missionService.create(request.getTitle(), request.getDescription(), request.getRewardContent(), request.getRewardCount(), request.getStartAt(),request.getEndAt(), request.getStoreId(), userId);
        return ResponseEntity.created(java.net.URI.create("/missions/" + missionId)).build();
    }
    //미션 수정
    @PatchMapping("/{missionId}")
    public ResponseEntity<Void> update(@Valid @RequestBody MissionUpdatedRequest request, @AuthenticationPrincipal LoginUser loginUser,
                                       @PathVariable Long missionId) {
        Long userId = loginUser.getId();
        missionService.update(missionId, request.getTitle(), request.getDescription(), request.getEndAt(), userId);
        return ResponseEntity.noContent().build();
    }

    //미션 삭제
    @DeleteMapping("/{missionId}")
    public ResponseEntity<Void> delete(@PathVariable Long missionId,
                                       @AuthenticationPrincipal LoginUser loginUser)
    {
        Long userId = loginUser.getId();
        missionService.delete(missionId, userId);
        return ResponseEntity.noContent().build();
    }

    //미션 단건 조회
    @GetMapping("/{missionId}")
    public ResponseEntity<MissionResponse> getById(@PathVariable Long missionId) {
        Mission mission = missionService.getOne(missionId);
        return ResponseEntity.ok(new MissionResponse(mission));
    }

    //진행 가능 미션 전체 조회
    @GetMapping("/joinable")
    public ResponseEntity<List<MissionResponse>> getJoinable()
    {
        List<MissionResponse> missions = missionService.getJoinable(LocalDateTime.now()).stream()
                .map(MissionResponse::new).toList();
        return ResponseEntity.ok(missions);
    }
    //미션 가게별 조회
    @GetMapping(params = "storeId")
    public ResponseEntity<List<MissionResponse>> getByStore(@RequestParam Long storeId) {
        List<MissionResponse> missions = missionService.getByStore(storeId).stream()
                .map(MissionResponse::new).toList();
        return ResponseEntity.ok(missions);
    }

    //미션 상태별 조회
    @GetMapping(params = "status")
    public ResponseEntity<List<MissionResponse>> getByStatus(@RequestParam MissionStatus status)
    {
        List<MissionResponse> missions = missionService.getByStatus(status, LocalDateTime.now()).stream()
                .map(MissionResponse::new).toList();
        return ResponseEntity.ok(missions);
    }
}
