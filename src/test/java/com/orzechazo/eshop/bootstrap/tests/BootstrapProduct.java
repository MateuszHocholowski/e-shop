package com.orzechazo.eshop.bootstrap.tests;

import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BootstrapProduct {
    private final List<Product> products = new ArrayList<>();
    private final ProductRepository productRepository;
    public static final String DB_PRODUCT_NAME = "dbProduct1";

    public BootstrapProduct(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void loadData() {
        log.debug("Loading Products Data");
        Product product1 = new Product();
        product1.setName(DB_PRODUCT_NAME);
        product1.setAmount(5);
        product1.setNetPrice(new BigDecimal("1.5"));
        product1.setGrossPrice(new BigDecimal("3.1"));
        product1.setDescription("testDescription1");
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("dbProduct2");
        product2.setAmount(7);
        product2.setNetPrice(new BigDecimal("2.3"));
        product2.setGrossPrice(new BigDecimal("5.1"));
        product2.setDescription("testDescription2");
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("dbProduct3");
        product3.setAmount(12);
        product3.setNetPrice(new BigDecimal("5.7"));
        product3.setGrossPrice(new BigDecimal("2.2"));
        product3.setDescription("testDescription3");
        productRepository.save(product3);

        products.addAll(List.of(product1,product2,product3));
    }

    public List<Product> getProducts() {
        return products;
    }
}
