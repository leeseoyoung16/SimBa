package com.simba.project01.voucher;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VoucherResponse
{
    private Long voucherId;
    private VoucherStatus vStatus;
    private LocalDateTime issuedAt;
    private LocalDateTime expireAt;
    private LocalDateTime usedAt;
    private Long userId;
    private Long missionId;

    public VoucherResponse(Voucher voucher) {
        this.voucherId = voucher.getId();
        this.vStatus = voucher.getVStatus();
        this.issuedAt = voucher.getIssuedAt();
        this.expireAt = voucher.getExpireAt();
        this.usedAt = voucher.getUsedAt();
        this.userId = voucher.getReview().getUser().getId();
        this.missionId = voucher.getReview().getMission().getId();
    }

}
