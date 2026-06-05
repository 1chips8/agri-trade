package com.agritrade.product;

import com.agritrade.common.BizException;
import com.agritrade.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/api/merchant/products")
    public Result<Product> create(@Valid @RequestBody Product product) {
        return Result.ok(productService.create(product));
    }

    @PutMapping("/api/merchant/products/{productId}")
    public Result<Void> update(@PathVariable Long productId, @Valid @RequestBody Product product) {
        validateProductId(productId);
        productService.update(productId, product);
        return Result.ok();
    }

    @GetMapping("/api/merchant/products")
    public Result<List<Product>> merchantProducts() {
        return Result.ok(productService.merchantProducts());
    }

    @PostMapping("/api/merchant/products/{productId}/on-sale")
    public Result<Void> onSale(@PathVariable Long productId) {
        validateProductId(productId);
        productService.changeSale(productId, "ON_SALE");
        return Result.ok();
    }

    @PostMapping("/api/merchant/products/{productId}/off-sale")
    public Result<Void> offSale(@PathVariable Long productId) {
        validateProductId(productId);
        productService.changeSale(productId, "OFF_SALE");
        return Result.ok();
    }

    @GetMapping("/api/admin/products/audit")
    public Result<List<Product>> auditList() {
        return Result.ok(productService.auditList());
    }

    @PostMapping("/api/admin/products/{productId}/approve")
    public Result<Void> approve(@PathVariable Long productId) {
        validateProductId(productId);
        productService.audit(productId, "APPROVED");
        return Result.ok();
    }

    @PostMapping("/api/admin/products/{productId}/reject")
    public Result<Void> reject(@PathVariable Long productId) {
        validateProductId(productId);
        productService.audit(productId, "REJECTED");
        return Result.ok();
    }

    @GetMapping("/api/products")
    public Result<List<Product>> products(@RequestParam(required = false) Long categoryId,
                                          @RequestParam(required = false) String keyword) {
        return Result.ok(productService.publicList(categoryId, keyword));
    }

    @GetMapping("/api/products/{productId}")
    public Result<Product> detail(@PathVariable Long productId) {
        validateProductId(productId);
        return Result.ok(productService.detail(productId));
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BizException("商品ID必须为正数");
        }
    }
}
