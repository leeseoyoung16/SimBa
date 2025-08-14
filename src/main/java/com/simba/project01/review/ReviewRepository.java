package com.simba.project01.review;

import com.simba.project01.mission.Mission;
import com.simba.project01.store.Store;
import com.simba.project01.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>
{
    boolean existsByUserAndMission(User user, Mission mission);
    List<Review> findByUserId(Long userId);
    List<Review> findByMissionStoreId(Long storeId);
}
