package com.agritrade.cart;

import cn.dev33.satoken.stp.StpUtil;
import com.agritrade.common.BizException;
import com.agritrade.common.Result;
import com.agritrade.product.Product;
import com.agritrade.product.ProductMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/cart/items")
@RequiredArgsConstructor
public class CartController {
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    @PostMapping
    public Result<CartItem> add(@Valid @RequestBody AddCartRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || !"ON_SALE".equals(product.getSaleStatus())) {
            throw new BizException("商品不可购买");
        }
        CartItem item = cartItemMapper.selectOne(Wrappers.<CartItem>lambdaQuery()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, request.getProductId()));
        if (item == null) {
            item = new CartItem();
            item.setUserId(userId);
            item.setProductId(product.getId());
            item.setMerchantId(product.getMerchantId());
            item.setQuantity(request.getQuantity());
            item.setSelected(1);
            cartItemMapper.insert(item);
        } else {
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemMapper.updateById(item);
        }
        return Result.ok(item);
    }

    @GetMapping
    public Result<List<CartItem>> list() {
        return Result.ok(cartItemMapper.selectList(Wrappers.<CartItem>lambdaQuery()
                .eq(CartItem::getUserId, StpUtil.getLoginIdAsLong())
                .orderByDesc(CartItem::getCreateTime)));
    }

    @PutMapping("/{cartItemId}")
    public Result<Void> updateQuantity(@PathVariable Long cartItemId, @Valid @RequestBody QuantityRequest request) {
        CartItem item = owned(cartItemId);
        item.setQuantity(request.getQuantity());
        cartItemMapper.updateById(item);
        return Result.ok();
    }

    @DeleteMapping("/{cartItemId}")
    public Result<Void> delete(@PathVariable Long cartItemId) {
        owned(cartItemId);
        cartItemMapper.deleteById(cartItemId);
        return Result.ok();
    }

    @DeleteMapping
    public Result<Void> clear() {
        cartItemMapper.delete(Wrappers.<CartItem>lambdaQuery().eq(CartItem::getUserId, StpUtil.getLoginIdAsLong()));
        return Result.ok();
    }

    @PutMapping("/{cartItemId}/selected")
    public Result<Void> selected(@PathVariable Long cartItemId, @Valid @RequestBody SelectedRequest request) {
        CartItem item = owned(cartItemId);
        item.setSelected(request.getSelected());
        cartItemMapper.updateById(item);
        return Result.ok();
    }

    @PutMapping("/selected")
    public Result<Void> selectAll(@Valid @RequestBody SelectedRequest request) {
        List<CartItem> items = cartItemMapper.selectList(Wrappers.<CartItem>lambdaQuery().eq(CartItem::getUserId, StpUtil.getLoginIdAsLong()));
        for (CartItem item : items) {
            item.setSelected(request.getSelected());
            cartItemMapper.updateById(item);
        }
        return Result.ok();
    }

    private CartItem owned(Long id) {
        CartItem item = cartItemMapper.selectById(id);
        if (item == null || !item.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new BizException("购物车项不存在");
        }
        return item;
    }

    @Data
    static class AddCartRequest {
        @NotNull
        private Long productId;
        @NotNull
        @Min(1)
        private Integer quantity = 1;
    }

    @Data
    static class QuantityRequest {
        @NotNull
        @Min(1)
        private Integer quantity;
    }

    @Data
    static class SelectedRequest {
        @NotNull
        @Min(0)
        @Max(1)
        private Integer selected;
    }
}
