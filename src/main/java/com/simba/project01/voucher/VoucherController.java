package com.simba.project01.voucher;

import com.simba.project01.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/itda")
public class VoucherController
{
    private final VoucherService voucherService;
    //삭제
    @DeleteMapping("/{voucherId}")
    public ResponseEntity<Void> delete(@PathVariable Long voucherId,
                                 @AuthenticationPrincipal LoginUser user) {
        voucherService.delete(user.getId(), voucherId);
        return ResponseEntity.noContent().build();
    }

    //본인 단건 조회
    @GetMapping("/me/vouchers/{voucherId}")
    public ResponseEntity<VoucherResponse> getById(@PathVariable Long voucherId, @AuthenticationPrincipal LoginUser user)
    {
        Voucher voucher = voucherService.getById(voucherId, user.getId());
        return ResponseEntity.ok(new VoucherResponse(voucher));
    }
    //본인 목록 상태별 조회
    @GetMapping("/me/vouchers")
    public ResponseEntity<List<VoucherResponse>> getByUser(@AuthenticationPrincipal LoginUser user, @RequestParam(defaultValue = "issued") String filter) {
        List<VoucherResponse> vouchers = voucherService.getByUser(user.getId(), filter).stream()
                .map(VoucherResponse::new)
                .toList();
        return ResponseEntity.ok(vouchers);
    }
    //바우처 사용
    @PatchMapping("me/vouchers/{voucherId}/use")
    public ResponseEntity<Void> useVoucher(@AuthenticationPrincipal LoginUser loginUser, @PathVariable Long voucherId) {
        voucherService.use(loginUser.getId(), voucherId);
        return ResponseEntity.noContent().build();
    }
}
