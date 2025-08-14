package com.simba.project01.review;

import com.simba.project01.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController
{
    private final ReviewService reviewService;

    //리뷰 생성
    @PostMapping(
            value ="/missions/{missionId}/reviews",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> create(@AuthenticationPrincipal LoginUser user,
                                       @PathVariable Long missionId, @Valid @RequestPart("request") ReviewCreateRequest request,
                                       @RequestPart(value = "image", required = false) MultipartFile image) throws IOException
    {
        Long userId = user.getId();
        Long newId = reviewService.create(missionId, userId, image, request.getRating(), request.getContent());
        return ResponseEntity.created(URI.create("/reviews/" + newId)).build();
    }
    //리뷰 수정
    @PatchMapping(
            value ="/reviews/{reviewId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(@AuthenticationPrincipal LoginUser user, @PathVariable Long reviewId
                                       , @Valid @RequestPart("request") ReviewUpdatedRequest request
                                       , @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        Long userId = user.getId();
        reviewService.update(reviewId, userId, image, request.getRating(), request.getContent());
        return ResponseEntity.noContent().build();
    }

    //리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal LoginUser user, @PathVariable Long reviewId) {
        Long userId = user.getId();
        reviewService.delete(userId, reviewId);
        return ResponseEntity.noContent().build();
    }
    //리뷰 승인
    @PatchMapping(value = "/reviews/{reviewId}/status")
    public ResponseEntity<Void> updateStatus(@AuthenticationPrincipal LoginUser user,
                                             @PathVariable Long reviewId, @RequestParam ReviewStatus status) {
        reviewService.approve(user.getId(), reviewId, status);
        return ResponseEntity.noContent().build();
    }

    //리뷰 단건 조회
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> getById(@PathVariable Long reviewId) {
        Review review = reviewService.getById(reviewId);
        return ResponseEntity.ok(new ReviewResponse(review));
    }

    //리뷰 사용자별 조회
    @GetMapping(value = "/reviews", params = "userId")
    public ResponseEntity<List<ReviewResponse>> getByUser(@RequestParam Long userId)
    {
        List<ReviewResponse> reviews = reviewService.getByUserId(userId).stream()
                .map(ReviewResponse::new)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    //리뷰 가게별 조회
    @GetMapping(value = "/reviews", params = "storeId")
    public ResponseEntity<List<ReviewResponse>> getByStore(@RequestParam Long storeId)
    {
        List<ReviewResponse> reviews = reviewService.getByStoreId(storeId).stream()
                .map(ReviewResponse::new)
                .toList();
        return ResponseEntity.ok(reviews);
    }
}
