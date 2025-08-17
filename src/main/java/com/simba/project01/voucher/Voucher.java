package com.simba.project01.voucher;

import com.simba.project01.review.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Voucher
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "v_status", nullable = false)
    private VoucherStatus vStatus;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt; //발급 시간

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt; //사용 시간

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id",nullable = false, unique = true)
    private Review review;

    @PrePersist
    void prePersist() {
        if (vStatus == null) vStatus = VoucherStatus.ISSUED;
        if (issuedAt == null) issuedAt = LocalDateTime.now();
        if (expireAt == null) expireAt = issuedAt.plusDays(7);
    }

}
