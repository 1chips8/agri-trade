package com.agritrade.merchant;

import com.agritrade.common.Result;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantService merchantService;

    @PostMapping("/api/merchant/apply")
    public Result<MerchantApply> apply(@RequestBody MerchantApply request) {
        return Result.ok(merchantService.apply(request));
    }

    @GetMapping("/api/merchant/apply/my")
    public Result<MerchantApply> myApply() {
        return Result.ok(merchantService.myApply());
    }

    @GetMapping("/api/admin/merchant-applications")
    public Result<List<MerchantApply>> list() {
        return Result.ok(merchantService.listApplications());
    }

    @GetMapping("/api/admin/merchant-applications/{applyId}")
    public Result<MerchantApply> detail(@PathVariable Long applyId) {
        return Result.ok(merchantService.listApplications().stream()
                .filter(item -> item.getId().equals(applyId))
                .findFirst()
                .orElse(null));
    }

    @PostMapping("/api/admin/merchant-applications/{applyId}/approve")
    public Result<Void> approve(@PathVariable Long applyId) {
        merchantService.approve(applyId);
        return Result.ok();
    }

    @PostMapping("/api/admin/merchant-applications/{applyId}/reject")
    public Result<Void> reject(@PathVariable Long applyId, @RequestBody RejectRequest request) {
        merchantService.reject(applyId, request.getRejectReason());
        return Result.ok();
    }

    @Data
    static class RejectRequest {
        private String rejectReason;
    }
}
