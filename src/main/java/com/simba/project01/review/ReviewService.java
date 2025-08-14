package com.simba.project01.review;

import com.simba.project01.mission.Mission;
import com.simba.project01.mission.MissionRepository;
import com.simba.project01.store.Store;
import com.simba.project01.store.StoreRepository;
import com.simba.project01.user.User;
import com.simba.project01.user.UserRepository;
import com.simba.project01.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService
{
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final StoreRepository storeRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    //리뷰 작성
    @Transactional
    public Long create(Long missionId, Long userId, MultipartFile img, int rating, String content) throws IOException
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Mission mission = missionRepository.findByIdForUpdate(missionId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 미션을 찾을 수 없습니다."));
        if (reviewRepository.existsByUserAndMission(user, mission)) {
            throw new IllegalArgumentException("이미 리뷰를 작성했습니다.");
        }
        // 보상 재고 확인 & 차감
        mission.decreaseRewardCount();

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

        Review review = new Review();
        review.setUser(user);
        review.setMission(mission);
        review.setRating(rating);
        review.setContent(content);
        review.setImgUrl(imageUrl);
        review.setStatus(ReviewStatus.PENDING);
        Review created = reviewRepository.save(review);
        return created.getId();
    }

    //리뷰 수정
    @Transactional
    public void update(Long reviewId, Long userId, MultipartFile img, Integer rating, String content) throws IOException
    {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 리뷰를 찾을 수 없습니다."));
        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new IllegalArgumentException("승인 후 리뷰는 수정할 수 없습니다.");
        }
        if(!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("리뷰를 작성한 본인만 수정 가능합니다.");
        }
        if (rating != null) {
            review.setRating(rating);
        }
        if (content != null && !content.isEmpty()) {
            review.setContent(content);
        }
        if (img != null && !img.isEmpty()) {
            //기존 파일 삭제
            if (review.getImgUrl() != null) {
                String oldFileName = Paths.get(review.getImgUrl()).getFileName().toString();
                File oldFile = new File(uploadDir, oldFileName);
                if (oldFile.exists()) oldFile.delete();
            }

            // 새 파일 저장
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = System.currentTimeMillis() + "_" + img.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            try {
                img.transferTo(filePath.toFile());
                review.setImgUrl("/upload/" + fileName);
            } catch (IOException e) {
                File f = filePath.toFile();
                if (f.exists()) f.delete();
                throw e;
            }
        }
    }
    //리뷰 삭제
    @Transactional
    public void delete(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        if (!user.getRole().equals(UserRole.ADMIN) && !review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("관리자 또는 작성자만 리뷰를 삭제할 수 있습니다.");
        }

        //삭제시 이미지도 제거
        if (review.getImgUrl() != null) {
            String fileName = Paths.get(review.getImgUrl()).getFileName().toString();
            File oldFile = new File(uploadDir, fileName);
            if (oldFile.exists()) oldFile.delete();
        }
        reviewRepository.delete(review);

    }
    //리뷰 승인
    @Transactional
    public void approve(Long userId, Long reviewId, ReviewStatus status) {
        if (status == null || status == ReviewStatus.PENDING) {
            throw new IllegalArgumentException("승인 상태는 APPROVED 또는 REJECTED만 가능합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        // 점주(해당 미션의 가게 소유자) 또는 ADMIN
        Long ownerId = review.getMission().getStore().getUser().getId();
        boolean isOwner = ownerId != null && ownerId.equals(userId);
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("가게 사장님 또는 관리자만 승인/거절할 수 있습니다.");
        }

        // PENDING에서만 변경 가능
        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 리뷰입니다.");
        }
        review.setStatus(status);
    }
    //유저별 리뷰 조회
    @Transactional(readOnly = true)
    public List<Review> getByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    //가게별 리뷰 조회
    @Transactional(readOnly = true)
    public List<Review> getByStoreId(Long storeId){
        return reviewRepository.findByMissionStoreId(storeId);
    }

    //단건 조회
    @Transactional(readOnly = true)
    public Review getById(Long reviewId)
    {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));
        return review;
    }
}
