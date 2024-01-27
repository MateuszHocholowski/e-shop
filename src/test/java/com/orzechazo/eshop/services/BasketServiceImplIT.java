package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.BootstrapBasket;
import com.orzechazo.eshop.bootstrap.tests.BootstrapProduct;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.assertj.core.api.Assert;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BasketServiceImplIT {

    private static final String PRODUCT_NOT_IN_DB = "productNotInDB";
    @Autowired
    private BasketRepository basketRepository;
    private BasketServiceImpl basketService;
    @Autowired
    private ProductRepository productRepository;
    public static final BigDecimal BASKET1_TOTAL_PRICE = new BigDecimal("120");
    private int DB_DEFAULT_BASKET_COUNT;
    private Set<String> basketProductNameSet;

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
        //given
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME);
        //when
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);

        BasketDto basketAfterAddition = basketService.getBasketDtoByBasketId(DB_BASKET_ID);

        basketProductNameSet = basketAfterAddition.getProductNamesMap().keySet();
        //then
        assertEquals(1,basketAfterAddition.getProductNamesMap().get(DB_PRODUCT1_NAME));
        assertEquals(DB_PRODUCT1_GROSS_PRICE,basketAfterAddition.getTotalPrice());
        assertThat(basketProductNameSet).containsExactlyInAnyOrderElementsOf(expectedProductNameSet);
    }

    @Test
    void addTwoDifferentProductsToBasket() {
        //given
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME, DB_PRODUCT2_NAME);
        //when
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);
        basketService.addProductToBasket(DB_PRODUCT2_NAME,DB_BASKET_ID);

        BasketDto basketAfterAddition = basketService.getBasketDtoByBasketId(DB_BASKET_ID);

        Set<String> basketProductNameSet = basketAfterAddition.getProductNamesMap().keySet();
        //then
        assertThat(basketProductNameSet).containsExactlyInAnyOrderElementsOf(expectedProductNameSet);
    }

    @Test
    void addProductToBasketTwiceWithDifferentAmount() {
        //given
        int expectedProductAmount = 5;
        BigDecimal expectedPrice = DB_PRODUCT1_GROSS_PRICE.multiply(new BigDecimal(expectedProductAmount));
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME);
        //when
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID, expectedProductAmount-1);

        BasketDto basketAfterAddition = basketService.getBasketDtoByBasketId(DB_BASKET_ID);

        basketProductNameSet = basketAfterAddition.getProductNamesMap().keySet();
        //then
        assertThat(basketProductNameSet).containsExactlyInAnyOrderElementsOf(expectedProductNameSet);
        assertEquals(expectedProductAmount,basketAfterAddition.getProductNamesMap().get(DB_PRODUCT1_NAME));
        assertEquals(expectedPrice,basketAfterAddition.getTotalPrice());
    }

    @Test
    void subtractProductFromBasket() {
        //given
        int initialProductAmount = 4;
        int expectedProductAmount = initialProductAmount - 1;
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME);
        BigDecimal expectedPrice = DB_PRODUCT1_GROSS_PRICE.multiply(new BigDecimal(expectedProductAmount));

        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID, initialProductAmount);
        //when
        basketService.subtractProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);

        BasketDto basketAfterSubtraction = basketService.getBasketDtoByBasketId(DB_BASKET_ID);

        basketProductNameSet = basketAfterSubtraction.getProductNamesMap().keySet();
        int finalProductAmountInBasket = basketAfterSubtraction.getProductNamesMap().get(DB_PRODUCT1_NAME);
        //then
        assertThat(basketProductNameSet).containsExactlyInAnyOrderElementsOf(expectedProductNameSet);
        assertEquals(expectedProductAmount,finalProductAmountInBasket);
        assertEquals(expectedPrice,basketAfterSubtraction.getTotalPrice());
    }

    @Test
    void subtractTheSameProductTwiceWithoutRemovingFromBasket() {
        //given
        int initialProductAmount = 4;
        int amountToSubtract = 3;
        int expectedProductAmount = initialProductAmount - amountToSubtract;
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME);
        BigDecimal expectedPrice = DB_PRODUCT1_GROSS_PRICE.multiply(new BigDecimal(expectedProductAmount));

        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID, initialProductAmount);
        //when
        basketService.subtractProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);
        basketService.subtractProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET_ID,amountToSubtract -1);

        BasketDto basketAfterSubtraction = basketService.getBasketDtoByBasketId(DB_BASKET_ID);

        basketProductNameSet = basketAfterSubtraction.getProductNamesMap().keySet();
        int finalProductAmountInBasket = basketAfterSubtraction.getProductNamesMap().get(DB_PRODUCT1_NAME);
        //then
        assertThat(basketProductNameSet).containsExactlyInAnyOrderElementsOf(expectedProductNameSet);
        assertEquals(expectedProductAmount,finalProductAmountInBasket);
        assertEquals(expectedPrice,basketAfterSubtraction.getTotalPrice());
    }

    @Test
    void removeProductFromBasket() {
        //given
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);
        //when
        basketService.removeProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);

        BasketDto basketAfterRemoval = basketService.getBasketDtoByBasketId(DB_BASKET_ID);

        basketProductNameSet = basketAfterRemoval.getProductNamesMap().keySet();
        //then
        assertThat(basketProductNameSet).isEmpty();
        assertEquals(new BigDecimal("0"),basketAfterRemoval.getTotalPrice());
    }

    @Test
    void removeOnlyOneOfProductsFromBasket() {
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME);

        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);
        basketService.addProductToBasket(DB_PRODUCT2_NAME, DB_BASKET_ID);
        //when
        basketService.removeProductFromBasket(DB_PRODUCT2_NAME, DB_BASKET_ID);

        BasketDto basketAfterRemoval = basketService.getBasketDtoByBasketId(DB_BASKET_ID);
        basketProductNameSet = basketAfterRemoval.getProductNamesMap().keySet();
        //then
        assertThat(basketProductNameSet).containsExactlyInAnyOrderElementsOf(expectedProductNameSet);
        assertEquals(DB_PRODUCT1_GROSS_PRICE,basketAfterRemoval.getTotalPrice());
    }

    @Test
    void subtractMoreOfProductThatIsInBasket() {
        //given
        int initialProductAmount = 4;
        int amountToSubtract = 6;
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID, initialProductAmount);
        //when
        basketService.subtractProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET_ID, amountToSubtract);

        BasketDto basketAfterSubtraction = basketService.getBasketDtoByBasketId(DB_BASKET_ID);

        basketProductNameSet = basketAfterSubtraction.getProductNamesMap().keySet();
        //then
        assertThat(basketProductNameSet).isEmpty();
        assertEquals(new BigDecimal("0"),basketAfterSubtraction.getTotalPrice());
    }

    @Test
    void tryToAddProductThatIsNotInDB() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> basketService.addProductToBasket(PRODUCT_NOT_IN_DB, DB_BASKET_ID));

        assertEquals("Product: " + PRODUCT_NOT_IN_DB + " doesn't exist in database.", exception.getMessage());
    }

    @Test
    void tryToSubtractProductThatIsNotInBasket() {
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME);

        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET_ID);
        //when
        basketService.subtractProductFromBasket(DB_PRODUCT3_NAME, DB_BASKET_ID);

        BasketDto basketAfterSubtraction = basketService.getBasketDtoByBasketId(DB_BASKET_ID);

        basketProductNameSet = basketAfterSubtraction.getProductNamesMap().keySet();
        //then
        assertThat(basketProductNameSet).containsExactlyInAnyOrderElementsOf(expectedProductNameSet);
        assertEquals(DB_PRODUCT1_GROSS_PRICE,basketAfterSubtraction.getTotalPrice());
    }
}