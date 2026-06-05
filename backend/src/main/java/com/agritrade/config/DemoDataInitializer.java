package com.agritrade.config;

import com.agritrade.merchant.Merchant;
import com.agritrade.merchant.MerchantMapper;
import com.agritrade.product.Product;
import com.agritrade.product.ProductMapper;
import com.agritrade.user.User;
import com.agritrade.user.UserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("local-h2")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "agri.demo-data", name = "enabled", havingValue = "true")
public class DemoDataInitializer implements CommandLineRunner {
    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;
    private final ProductMapper productMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User buyer = ensureUser("buyer", "buyer123", "13900000001", "演示买家", "USER");
        User merchantUser = ensureUser("merchant", "merchant123", "13900000002", "演示商家", "MERCHANT");
        Merchant merchant = ensureMerchant(merchantUser);
        ensureProduct(merchant, 1L, "有机番茄", "山东寿光", "新鲜采摘的沙瓤番茄，适合生食和烹饪。", "斤",
                new BigDecimal("6.80"), 120,
                "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=800&q=80");
        ensureProduct(merchant, 2L, "赣南脐橙", "江西赣州", "果香浓郁，酸甜平衡，适合家庭囤货。", "箱",
                new BigDecimal("58.00"), 45,
                "https://images.unsplash.com/photo-1582979512210-99b6a53386f9?auto=format&fit=crop&w=800&q=80");
        ensureProduct(merchant, 3L, "东北五常大米", "黑龙江五常", "颗粒饱满，米香明显，适合日常主食。", "袋",
                new BigDecimal("89.00"), 80,
                "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=800&q=80");
    }

    private User ensureUser(String username, String password, String phone, String nickname, String role) {
        User existing = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (existing != null) {
            return existing;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setNickname(nickname);
        user.setRole(role);
        user.setStatus("ENABLED");
        userMapper.insert(user);
        return user;
    }

    private Merchant ensureMerchant(User merchantUser) {
        Merchant existing = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getUserId, merchantUser.getId()));
        if (existing != null) {
            return existing;
        }
        Merchant merchant = new Merchant();
        merchant.setUserId(merchantUser.getId());
        merchant.setMerchantName("绿野鲜农演示店");
        merchant.setContactName("王店长");
        merchant.setContactPhone("13900000002");
        merchant.setOriginAddress("山东寿光");
        merchant.setDescription("演示用认证商家，主营蔬菜、水果和粮油。");
        merchant.setStatus("ENABLED");
        merchant.setTotalSalesAmount(BigDecimal.ZERO);
        merchant.setTotalOrderCount(0);
        merchantMapper.insert(merchant);
        return merchant;
    }

    private void ensureProduct(Merchant merchant, Long categoryId, String name, String originPlace, String description,
                               String unit, BigDecimal price, int stock, String image) {
        Long count = productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchant.getId())
                .eq(Product::getProductName, name));
        if (count > 0) {
            return;
        }
        Product product = new Product();
        product.setMerchantId(merchant.getId());
        product.setCategoryId(categoryId);
        product.setProductName(name);
        product.setProductImage(image);
        product.setDescription(description);
        product.setOriginPlace(originPlace);
        product.setPrice(price);
        product.setStock(stock);
        product.setUnit(unit);
        product.setShelfLife("7天");
        product.setAuditStatus("APPROVED");
        product.setSaleStatus("ON_SALE");
        product.setSalesCount(0);
        product.setReviewCount(0);
        product.setAverageRating(BigDecimal.ZERO);
        productMapper.insert(product);
    }
}
