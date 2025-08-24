package com.simba.project01.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>
{
    boolean existsByLatitudeAndLongitude(BigDecimal latitude, BigDecimal longitude);

    List<Store> findByCategory(StoreCategory category);

    @Query("SELECT s FROM Store s WHERE s.user.id = :userId")
    List<Store> findAllByUserId(@Param("userId") Long userId);
}
