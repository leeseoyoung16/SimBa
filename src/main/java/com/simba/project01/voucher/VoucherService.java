package com.simba.project01.voucher;

import com.simba.project01.user.User;
import com.simba.project01.user.UserRepository;
import com.simba.project01.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService
{
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;

    //바우처 삭제 (관리자)
    @Transactional
    public void delete(Long userId, Long voucherId)
    {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 바우처입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("관리자만 바우처를 삭제할 수 있습니다.");
        }
        voucherRepository.deleteById(voucherId);
    }

    //바우처 목록 조회 (본인) active || all
    @Transactional
    public List<Voucher> getByUser(Long userId, String filter)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        voucherRepository.expireAllIssuedByUserBefore(userId, LocalDateTime.now(),VoucherStatus.ISSUED,VoucherStatus.EXPIRED);

        return switch (filter.toLowerCase()) {
            case "issued" -> voucherRepository.findUsableByUser(userId, LocalDateTime.now(), VoucherStatus.ISSUED);
            case "used" -> voucherRepository.findByReviewUserIdAndVStatus(userId, VoucherStatus.USED);
            case "expired" -> voucherRepository.findByReviewUserIdAndVStatus(userId, VoucherStatus.USED);
            case "all" -> voucherRepository.findByReviewUserId(userId);
            default ->  throw new IllegalStateException("지원하지 않는 filter 입니다.");
        };
    }

    //바우처 단건 조회(본인)
    @Transactional
    public Voucher getById(Long voucherId, Long userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        voucherRepository.expireAllIssuedByUserBefore(userId, LocalDateTime.now(),VoucherStatus.ISSUED,VoucherStatus.EXPIRED);

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 바우처입니다."));

        if(!voucher.getReview().getUser().getId().equals(userId))
            throw new AccessDeniedException("본인의 바우처만 조회 가능합니다.");
        return voucher;
    }

    //바우처 상태 변경 (본인)
    @Transactional
    public void use(Long userId, Long voucherId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 바우처입니다."));
        if (!voucher.getReview().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("바우처는 본인만 사용할 수 있습니다.");
        }

        if (voucher.getExpireAt() != null && LocalDateTime.now().isAfter(voucher.getExpireAt())) {
            voucher.setVStatus(VoucherStatus.EXPIRED);
            throw new IllegalStateException("만료된 바우처입니다.");
        }

        if (voucher.getVStatus() != VoucherStatus.ISSUED) {
            throw new IllegalArgumentException("바우처를 사용할 수 없습니다.");
        }
        voucher.setVStatus(VoucherStatus.USED);
        voucher.setUsedAt(LocalDateTime.now());
    }

}
