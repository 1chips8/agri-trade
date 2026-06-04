package com.agritrade.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local-h2")
@Transactional
class ProductMapperStockTest {
    @Autowired
    private ProductMapper productMapper;

    @Test
    void decrementStockIfAvailableUpdatesStockAtomically() {
        Product product = insertProduct(10, "ON_SALE");

        int affectedRows = productMapper.decrementStockIfAvailable(product.getId(), 4);

        assertThat(affectedRows).isEqualTo(1);
        assertThat(productMapper.selectById(product.getId()).getStock()).isEqualTo(6);
    }

    @Test
    void decrementStockIfAvailableRejectsInsufficientStock() {
        Product product = insertProduct(3, "ON_SALE");

        int affectedRows = productMapper.decrementStockIfAvailable(product.getId(), 4);

        assertThat(affectedRows).isZero();
        assertThat(productMapper.selectById(product.getId()).getStock()).isEqualTo(3);
    }

    @Test
    void decrementStockIfAvailableRejectsOffSaleProduct() {
        Product product = insertProduct(10, "OFF_SALE");

        int affectedRows = productMapper.decrementStockIfAvailable(product.getId(), 4);

        assertThat(affectedRows).isZero();
        assertThat(productMapper.selectById(product.getId()).getStock()).isEqualTo(10);
    }

    @Test
    void decrementStockIfAvailableRejectsNonPositiveQuantity() {
        Product product = insertProduct(10, "ON_SALE");

        int affectedRows = productMapper.decrementStockIfAvailable(product.getId(), -4);

        assertThat(affectedRows).isZero();
        assertThat(productMapper.selectById(product.getId()).getStock()).isEqualTo(10);
    }

    @Test
    void incrementStockAddsToCurrentStock() {
        Product product = insertProduct(6, "ON_SALE");

        int affectedRows = productMapper.incrementStock(product.getId(), 4);

        assertThat(affectedRows).isEqualTo(1);
        assertThat(productMapper.selectById(product.getId()).getStock()).isEqualTo(10);
    }

    @Test
    void incrementStockRejectsNonPositiveQuantity() {
        Product product = insertProduct(6, "ON_SALE");

        int affectedRows = productMapper.incrementStock(product.getId(), 0);

        assertThat(affectedRows).isZero();
        assertThat(productMapper.selectById(product.getId()).getStock()).isEqualTo(6);
    }

    private Product insertProduct(int stock, String saleStatus) {
        Product product = new Product();
        product.setMerchantId(1L);
        product.setCategoryId(1L);
        product.setProductName("原子库存测试商品");
        product.setPrice(new BigDecimal("12.50"));
        product.setStock(stock);
        product.setAuditStatus("APPROVED");
        product.setSaleStatus(saleStatus);
        productMapper.insert(product);
        return product;
    }
}
