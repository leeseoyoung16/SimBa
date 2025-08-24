package com.simba.project01.store;

import com.simba.project01.LoginUser;
import com.simba.project01.review.ReviewSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/itda")
public class StoreController
{
    private final StoreService storeService;
    private final ReviewSummaryService reviewSummaryService;

    //등록
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/stores")
    public ResponseEntity<Void> createStore(@RequestBody @Valid StoreCreateRequest createRequest, @AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getId();
        Long storeId = storeService.create(createRequest.getName(), createRequest.getLatitude(),
                createRequest.getLongitude(), createRequest.getCategory(), userId, createRequest.getBusinessNumber(), createRequest.getAddress());
        return ResponseEntity.created(URI.create("/itda/stores/" + storeId)).build();
    }

    //수정
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/stores/{storeId}")
    public ResponseEntity<Void> updateStore(@PathVariable Long storeId,
                                         @RequestBody StoreCreateRequest request,
                                         @AuthenticationPrincipal LoginUser loginUser) {
        storeService.update(storeId, loginUser.getId(), request.getName(), request.getLatitude(),
                request.getLongitude(), request.getCategory(), request.getBusinessNumber(), request.getAddress());
        return ResponseEntity.noContent().build();
    }


    //삭제 (관리자, 자기 소유)
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @DeleteMapping("/stores/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long storeId, @AuthenticationPrincipal LoginUser loginUser)
    {
        storeService.delete(storeId, loginUser.getId());
        return ResponseEntity.noContent().build();
    }

    //전체 조회
    @GetMapping("/stores")
    public ResponseEntity<List<StoreResponse>> getStoreAll()
    {
        List<StoreResponse> stores = storeService.findAll().stream()
                .map(StoreResponse::new)
                .toList();
        return ResponseEntity.ok(stores);
    }

    //단건 조회
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<StoreResponse> getStore(@PathVariable Long storeId)
    {
        Store store = storeService.findById(storeId);
        return ResponseEntity.ok(new StoreResponse(store));
    }

    //카테고리별 조회
    @GetMapping(value = "/stores",
            params = "category")
    public ResponseEntity<List<StoreResponse>> getStoreByCategory(@RequestParam StoreCategory category) {
        List<StoreResponse> stores = storeService.findByCategory(category).stream()
                .map(StoreResponse::new)
                .toList();
        return ResponseEntity.ok(stores);
    }

    //가게 별 리뷰 요약
    @GetMapping("/stores/{storeId}/summary")
    public ResponseEntity<String> getReviewSummary(@PathVariable Long storeId)
    {
        String summary = reviewSummaryService.summarizeReviews(storeId);
        return ResponseEntity.ok(summary);
    }

    //사용자 별 가게
    @GetMapping("/me/stores")
    public ResponseEntity<List<StoreResponse>> getStoresByUser(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getId();
        List<StoreResponse> stores = storeService.findByUser(userId).stream()
                .map(StoreResponse::new)
                .toList();
        return ResponseEntity.ok(stores);
    }

}
