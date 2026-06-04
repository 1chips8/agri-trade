package com.agritrade.merchant;

import cn.dev33.satoken.stp.StpUtil;
import com.agritrade.auth.AuthService;
import com.agritrade.common.BizException;
import com.agritrade.user.User;
import com.agritrade.user.UserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantApplyMapper applyMapper;
    private final MerchantMapper merchantMapper;
    private final UserMapper userMapper;
    private final AuthService authService;

    public MerchantApply apply(MerchantApply request) {
        Long userId = StpUtil.getLoginIdAsLong();
        MerchantApply open = applyMapper.selectOne(Wrappers.<MerchantApply>lambdaQuery()
                .eq(MerchantApply::getUserId, userId)
                .eq(MerchantApply::getApplyStatus, "PENDING"));
        if (open != null) {
            throw new BizException("已有待审核申请");
        }
        request.setUserId(userId);
        request.setApplyStatus("PENDING");
        applyMapper.insert(request);
        return request;
    }

    public MerchantApply myApply() {
        return applyMapper.selectOne(Wrappers.<MerchantApply>lambdaQuery()
                .eq(MerchantApply::getUserId, StpUtil.getLoginIdAsLong())
                .orderByDesc(MerchantApply::getCreateTime)
                .last("limit 1"));
    }

    public List<MerchantApply> listApplications() {
        authService.requireRole("ADMIN");
        return applyMapper.selectList(Wrappers.<MerchantApply>lambdaQuery().orderByDesc(MerchantApply::getCreateTime));
    }

    @Transactional
    public void approve(Long applyId) {
        authService.requireRole("ADMIN");
        MerchantApply apply = applyMapper.selectById(applyId);
        if (apply == null || !"PENDING".equals(apply.getApplyStatus())) {
            throw new BizException("申请状态不可审核");
        }
        apply.setApplyStatus("APPROVED");
        apply.setReviewerId(StpUtil.getLoginIdAsLong());
        apply.setReviewTime(LocalDateTime.now());
        applyMapper.updateById(apply);

        Merchant merchant = new Merchant();
        merchant.setUserId(apply.getUserId());
        merchant.setMerchantName(apply.getMerchantName());
        merchant.setContactName(apply.getContactName());
        merchant.setContactPhone(apply.getContactPhone());
        merchant.setOriginAddress(apply.getOriginAddress());
        merchant.setDescription(apply.getDescription());
        merchant.setLicenseImage(apply.getLicenseImage());
        merchant.setStatus("ENABLED");
        merchant.setTotalSalesAmount(BigDecimal.ZERO);
        merchant.setTotalOrderCount(0);
        merchantMapper.insert(merchant);

        User user = userMapper.selectById(apply.getUserId());
        user.setRole("MERCHANT");
        userMapper.updateById(user);
    }

    public void reject(Long applyId, String reason) {
        authService.requireRole("ADMIN");
        MerchantApply apply = applyMapper.selectById(applyId);
        if (apply == null || !"PENDING".equals(apply.getApplyStatus())) {
            throw new BizException("申请状态不可审核");
        }
        apply.setApplyStatus("REJECTED");
        apply.setRejectReason(reason);
        apply.setReviewerId(StpUtil.getLoginIdAsLong());
        apply.setReviewTime(LocalDateTime.now());
        applyMapper.updateById(apply);
    }

    public Merchant currentMerchant() {
        Merchant merchant = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getUserId, StpUtil.getLoginIdAsLong()));
        if (merchant == null) {
            throw new BizException("当前用户不是商家");
        }
        return merchant;
    }
}
