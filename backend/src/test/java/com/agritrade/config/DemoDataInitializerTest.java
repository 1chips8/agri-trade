package com.agritrade.config;

import com.agritrade.merchant.Merchant;
import com.agritrade.merchant.MerchantMapper;
import com.agritrade.product.Product;
import com.agritrade.product.ProductMapper;
import com.agritrade.user.User;
import com.agritrade.user.UserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "agri.demo-data.enabled=true")
@ActiveProfiles("local-h2")
class DemoDataInitializerTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private ProductMapper productMapper;

    @Test
    void createsDemoUsersMerchantAndProductsWhenEnabled() {
        User buyer = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, "buyer"));
        User merchantUser = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, "merchant"));
        Merchant merchant = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getMerchantName, "绿野鲜农演示店"));
        Long productCount = productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchant.getId())
                .eq(Product::getAuditStatus, "APPROVED")
                .eq(Product::getSaleStatus, "ON_SALE"));

        assertThat(buyer).isNotNull();
        assertThat(merchantUser).isNotNull();
        assertThat(merchant).isNotNull();
        assertThat(productCount).isGreaterThanOrEqualTo(3);
    }
}
