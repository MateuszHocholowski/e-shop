package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.BootstrapBasket;
import com.orzechazo.eshop.bootstrap.tests.BootstrapProduct;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Set;

import static com.orzechazo.eshop.bootstrap.tests.BootstrapBasket.DB_BASKET_ID;
import static com.orzechazo.eshop.bootstrap.tests.BootstrapProduct.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BasketServiceImplIT {

    @Autowired
    private BasketRepository basketRepository;
    private BasketServiceImpl basketService;
    @Autowired
    private ProductRepository productRepository;
    public static final BigDecimal BASKET1_TOTAL_PRICE = new BigDecimal("120");
    private int DB_DEFAULT_BASKET_COUNT;

    @BeforeEach
    void setUp() {
        BootstrapBasket bootstrapBasket = new BootstrapBasket(basketRepository);
        bootstrapBasket.loadData();
        BootstrapProduct bootstrapProduct = new BootstrapProduct(productRepository);
        bootstrapProduct.loadData();

        basketService = new BasketServiceImpl(basketRepository, productRepository);
        DB_DEFAULT_BASKET_COUNT = bootstrapBasket.getBaskets().size();
    }

    @Test
    void addSingleProductToEmptyBasket() {
        //when
        BasketDto basketAfterAddition = basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);
        //then
        assertEquals(1,basketAfterAddition.getProductNamesMap().get(DB_PRODUCT1_NAME));
        assertEquals(DB_PRODUCT1_GROSS_PRICE,basketAfterAddition.getTotalPrice());
        assertThat(basketAfterAddition.getProductNamesMap().keySet())
                .containsExactlyInAnyOrderElementsOf(Set.of(DB_PRODUCT1_NAME));
    }

    @Test
    void addTwoProductsToBasket() {
        //when
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);
        BasketDto basketAfterAddition = basketService.addProductToBasket(DB_PRODUCT2_NAME,DB_BASKET_ID);
        //then
        assertThat(basketAfterAddition.getProductNamesMap().keySet())
                .containsExactlyInAnyOrderElementsOf(Set.of(DB_PRODUCT1_NAME, DB_PRODUCT2_NAME));


    }
}