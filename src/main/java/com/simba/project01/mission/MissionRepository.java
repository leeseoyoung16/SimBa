package com.simba.project01.mission;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long>
{
    List<Mission> findByStoreIdOrderByStartAtDesc(Long storeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Mission m where m.id = :id")
    Optional<Mission> findByIdForUpdate(@Param("id") Long id);

    @Query("""
    select m
    from Mission m
    where :now between m.startAt and m.endAt
      and m.rewardRemainingCount > 0
    order by m.startAt desc
    """)
    List<Mission> findJoinable(@Param("now") LocalDateTime now);

    @Query("select m from Mission m where m.startAt > :now order by m.startAt asc")
    List<Mission> findScheduled(LocalDateTime now);

    @Query("select m from Mission m where :now between m.startAt and m.endAt order by m.startAt asc")
    List<Mission> findOngoing(LocalDateTime now);

    @Query("select m from Mission m where m.endAt < :now order by m.endAt desc")
    List<Mission> findEnded(LocalDateTime now);

}
