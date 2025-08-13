package com.simba.project01.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long>
{
    List<Mission> findByStoreIdOrderByStartAtDesc(Long storeId);

    // 진행 중 + 보상 남음 (승인 리뷰 수 기반)
    @Query("""
    select m from Mission m
    where :now between m.startAt and m.endAt
      and (m.rewardTotalCount >
           (select count(r) from Review r where r.mission = m and r.approved = true))
    order by m.startAt desc
    """)
    List<Mission> findJoinable(LocalDateTime now);

    @Query("select m from Mission m where m.startAt > :now order by m.startAt asc")
    List<Mission> findScheduled(LocalDateTime now);

    @Query("select m from Mission m where :now between m.startAt and m.endAt order by m.startAt asc")
    List<Mission> findOngoing(LocalDateTime now);

    @Query("select m from Mission m where m.endAt < :now order by m.endAt desc")
    List<Mission> findEnded(LocalDateTime now);

}
