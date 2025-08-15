package com.simba.project01.voucher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long>
{
    Optional<Voucher> findByReviewId(Long reviewId);

    @Query("select v from Voucher v where v.review.user.id = :userId and v.vStatus = :status")
    List<Voucher> findByReviewUserIdAndVStatus(@Param("userId") Long userId, @Param("status") VoucherStatus status);

    List<Voucher> findByReviewUserId(Long userId);

    // 사용자 '사용 가능' 바우처: 상태=ISSUED && (만료일 없음 or 만료일 >= now)
    @Query("""
        select v from Voucher v
         where v.review.user.id = :userId
           and v.vStatus = :issued
           and v.expireAt >= :now
         order by v.expireAt asc
    """)
    List<Voucher> findUsableByUser(@Param("userId") Long userId,
                                   @Param("now") LocalDateTime now,
                                   @Param("issued") VoucherStatus issued);

    // 사용자 기준 만료 일괄 반영: ISSUED -> EXPIRED
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Voucher v
           set v.vStatus = :expired
         where v.vStatus = :issued
           and v.expireAt < :now
           and v.review.user.id = :userId
    """)
    int expireAllIssuedByUserBefore(@Param("userId") Long userId,
                                    @Param("now") LocalDateTime now,
                                    @Param("issued") VoucherStatus issued,
                                    @Param("expired") VoucherStatus expired);


}
