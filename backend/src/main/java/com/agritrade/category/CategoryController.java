package com.agritrade.category;

import com.agritrade.auth.AuthService;
import com.agritrade.common.BizException;
import com.agritrade.common.Result;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final ProductCategoryMapper categoryMapper;
    private final AuthService authService;

    @GetMapping("/api/product-categories")
    public Result<List<ProductCategory>> enabled() {
        return Result.ok(categoryMapper.selectList(Wrappers.<ProductCategory>lambdaQuery()
                .eq(ProductCategory::getStatus, "ENABLED")
                .orderByAsc(ProductCategory::getSortOrder)));
    }

    @PostMapping("/api/admin/product-categories")
    public Result<ProductCategory> create(@Valid @RequestBody ProductCategory category) {
        authService.requireRole("ADMIN");
        category.setStatus(category.getStatus() == null ? "ENABLED" : category.getStatus());
        categoryMapper.insert(category);
        return Result.ok(category);
    }

    @PutMapping("/api/admin/product-categories/{categoryId}")
    public Result<Void> update(@PathVariable Long categoryId, @Valid @RequestBody ProductCategory category) {
        authService.requireRole("ADMIN");
        category.setId(categoryId);
        categoryMapper.updateById(category);
        return Result.ok();
    }

    @PostMapping("/api/admin/product-categories/{categoryId}/status")
    public Result<Void> status(@PathVariable Long categoryId, @RequestParam String status) {
        validateStatus(status);
        authService.requireRole("ADMIN");
        ProductCategory category = new ProductCategory();
        category.setId(categoryId);
        category.setStatus(status);
        categoryMapper.updateById(category);
        return Result.ok();
    }

    private void validateStatus(String status) {
        if (!"ENABLED".equals(status) && !"DISABLED".equals(status)) {
            throw new BizException("类目状态无效");
        }
    }
}
