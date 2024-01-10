package com.orzechazo.eshop.bootstrap.tests;

import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Slf4j
@Component
public class BootstrapProduct {
    private final ProductRepository productRepository;

    public BootstrapProduct(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void loadData() {
        log.debug("Loading test data...");
        setProductData();
        log.debug("Data loaded.");
    }
    private void setProductData() {
        log.debug("Loading products...");

        Product product1 = new Product();
        product1.setName("product1");
        product1.setAmount(5);
        product1.setNetPrice(new BigDecimal("1.5"));
        product1.setGrossPrice(new BigDecimal("3.1"));
        product1.setDescription("testDescription1");
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("product2");
        product2.setAmount(7);
        product2.setNetPrice(new BigDecimal("2.3"));
        product2.setGrossPrice(new BigDecimal("5.1"));
        product2.setDescription("testDescription2");
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("product3");
        product3.setAmount(12);
        product3.setNetPrice(new BigDecimal("5.7"));
        product3.setGrossPrice(new BigDecimal("2.2"));
        product3.setDescription("testDescription3");
        productRepository.save(product3);

        log.debug("Products loaded: " + productRepository.count());
    }

}
