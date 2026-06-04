package com.agritrade.product;

import com.agritrade.auth.AuthService;
import com.agritrade.common.BizException;
import com.agritrade.merchant.Merchant;
import com.agritrade.merchant.MerchantService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper productMapper;
    private final MerchantService merchantService;
    private final AuthService authService;

    public Product create(Product product) {
        Merchant merchant = merchantService.currentMerchant();
        product.setMerchantId(merchant.getId());
        product.setAuditStatus("PENDING");
        product.setSaleStatus("OFF_SALE");
        product.setSalesCount(0);
        product.setReviewCount(0);
        product.setAverageRating(BigDecimal.ZERO);
        productMapper.insert(product);
        return product;
    }

    public void update(Long id, Product product) {
        Merchant merchant = merchantService.currentMerchant();
        Product old = productMapper.selectById(id);
        if (old == null || !merchant.getId().equals(old.getMerchantId())) {
            throw new BizException("商品不存在");
        }
        product.setId(id);
        product.setMerchantId(merchant.getId());
        product.setAuditStatus("PENDING");
        product.setSaleStatus("OFF_SALE");
        productMapper.updateById(product);
    }

    public List<Product> merchantProducts() {
        Merchant merchant = merchantService.currentMerchant();
        return productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchant.getId())
                .orderByDesc(Product::getCreateTime));
    }

    public void changeSale(Long id, String saleStatus) {
        Merchant merchant = merchantService.currentMerchant();
        Product product = productMapper.selectById(id);
        if (product == null || !merchant.getId().equals(product.getMerchantId())) {
            throw new BizException("商品不存在");
        }
        if (!"APPROVED".equals(product.getAuditStatus())) {
            throw new BizException("商品未审核通过");
        }
        product.setSaleStatus(saleStatus);
        productMapper.updateById(product);
    }

    public List<Product> auditList() {
        authService.requireRole("ADMIN");
        return productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getAuditStatus, "PENDING")
                .orderByDesc(Product::getCreateTime));
    }

    public void audit(Long id, String auditStatus) {
        authService.requireRole("ADMIN");
        Product product = productMapper.selectById(id);
        if (product == null || !"PENDING".equals(product.getAuditStatus())) {
            throw new BizException("商品状态不可审核");
        }
        product.setAuditStatus(auditStatus);
        productMapper.updateById(product);
    }

    public List<Product> publicList(Long categoryId, String keyword) {
        return productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getAuditStatus, "APPROVED")
                .eq(Product::getSaleStatus, "ON_SALE")
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .like(keyword != null && !keyword.isBlank(), Product::getProductName, keyword)
                .orderByDesc(Product::getSalesCount)
                .orderByDesc(Product::getCreateTime));
    }

    public Product detail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || !"APPROVED".equals(product.getAuditStatus())) {
            throw new BizException("商品不存在");
        }
        return product;
    }
}
