package com.simba.project01.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>
{
    boolean existsByLatitudeAndLongitude(BigDecimal latitude, BigDecimal longitude);

    List<Store> findByCategory(StoreCategory category);
}
