package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.Bootstrap;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.ProductRepository;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.repositories.ProductRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orzechazo.eshop.bootstrap.tests.Bootstrap.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BasketServiceImplIT {
    private static final String PRODUCT_NOT_IN_DB = "productNotInDB";
    @Autowired
    private BasketRepository basketRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    private BasketServiceImpl basketService;
    private int DB_DEFAULT_BASKET_COUNT;
    private Set<String> basketProductNameSet;

    @BeforeEach
    void setUp() {
        Bootstrap bootstrap = new Bootstrap(orderRepository,userRepository,productRepository,basketRepository);
        bootstrap.loadData();

        basketService = new BasketServiceImpl(basketRepository, productRepository);
        DB_DEFAULT_BASKET_COUNT = bootstrap.getBaskets().size();
    }

    @Test
    void getBasketByBasketId() {
        //when
        BasketDto returnedDto = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);
        //then
        assertEquals(DB_BASKET1_ID,returnedDto.getBasketId());
        assertEquals(DB_BASKET1_TOTAL_PRICE,returnedDto.getTotalPrice());
    }

    @Test
    void getBasketByBasketIdNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> basketService.getBasketDtoByBasketId("basketNotInDb"));
        assertEquals("Basket: basketNotInDb doesn't exist in database",exception.getMessage());
    }

    @Test
    void createBasket() {
        //given
        BasketDto basketDto = BasketDto.builder().totalPrice(new BigDecimal("430")).build();
        //when
        BasketDto createdDto = basketService.createBasket(basketDto);
        //then
        assertEquals(new BigDecimal("430"),createdDto.getTotalPrice());
        assertNotNull(createdDto.getBasketId());
        assertEquals(DB_DEFAULT_BASKET_COUNT+1,basketRepository.count());
    }

    @Test
    void createBasketExistingId() {
        BasketDto basketDto = BasketDto.builder().basketId(DB_BASKET1_ID).build();
        Exception exception = assertThrows(BadRequestException.class,
                () -> basketService.createBasket(basketDto));
        assertEquals("Basket already has an id: " + DB_BASKET1_ID,exception.getMessage());
    }

    @Test
    void updateBasket() {
        //given
        BasketDto basketToUpdate = BasketDto.builder().basketId(DB_BASKET1_ID)
                .totalPrice(new BigDecimal("540")).build();
        //when
        BasketDto updatedBasket = basketService.updateBasket(basketToUpdate);
        //then
        assertEquals(DB_BASKET1_ID,updatedBasket.getBasketId());
        assertEquals(new BigDecimal("540"),updatedBasket.getTotalPrice());
        assertEquals(DB_DEFAULT_BASKET_COUNT,basketRepository.count());
    }

    @Test
    void updateBasketNotFound() {
        BasketDto basketDto = BasketDto.builder().basketId("basketNotInDb").build();
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> basketService.updateBasket(basketDto));
        assertEquals("Basket: basketNotInDb doesn't exist in database",exception.getMessage());
    }

    @Test
    void deleteBasket() {
        //when
        basketService.deleteBasket(DB_BASKET1_ID);
        //then
        assertEquals(DB_DEFAULT_BASKET_COUNT-1,basketRepository.count());
    }

    @Test
    void addSingleProductToEmptyBasket() {
        //given
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME);
        //when
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, 1);

        BasketDto basketAfterAddition = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);

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
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, 1);
        basketService.addProductToBasket(DB_PRODUCT2_NAME, DB_BASKET1_ID, 1);

        BasketDto basketAfterAddition = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);

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
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, 1);
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, expectedProductAmount-1);

        BasketDto basketAfterAddition = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);

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

        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, initialProductAmount);
        //when
        basketService.subtractProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, 1);

        BasketDto basketAfterSubtraction = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);

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

        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, initialProductAmount);
        //when
        basketService.subtractProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, 1);
        basketService.subtractProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID,amountToSubtract -1);

        BasketDto basketAfterSubtraction = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);

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
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, 1);
        //when
        basketService.removeProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID);

        BasketDto basketAfterRemoval = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);

        basketProductNameSet = basketAfterRemoval.getProductNamesMap().keySet();
        //then
        assertThat(basketProductNameSet).isEmpty();
        assertEquals(new BigDecimal("0"),basketAfterRemoval.getTotalPrice());
    }

    @Test
    void removeOnlyOneOfProductsFromBasket() {
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME);

        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, 1);
        basketService.addProductToBasket(DB_PRODUCT2_NAME, DB_BASKET1_ID, 1);
        //when
        basketService.removeProductFromBasket(DB_PRODUCT2_NAME, DB_BASKET1_ID);

        BasketDto basketAfterRemoval = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);
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
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, initialProductAmount);
        //when
        basketService.subtractProductFromBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, amountToSubtract);

        BasketDto basketAfterSubtraction = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);

        basketProductNameSet = basketAfterSubtraction.getProductNamesMap().keySet();
        //then
        assertThat(basketProductNameSet).isEmpty();
        assertEquals(new BigDecimal("0"),basketAfterSubtraction.getTotalPrice());
    }

    @Test
    void tryToAddProductThatIsNotInDB() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> basketService.addProductToBasket(PRODUCT_NOT_IN_DB, DB_BASKET1_ID, 1));

        assertEquals("Product: " + PRODUCT_NOT_IN_DB + " doesn't exist in database.", exception.getMessage());
    }

    @Test
    void tryToSubtractProductThatIsNotInBasket() {
        Set<String> expectedProductNameSet = Set.of(DB_PRODUCT1_NAME);

        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, 1);
        //when
        basketService.subtractProductFromBasket(DB_PRODUCT3_NAME, DB_BASKET1_ID, 1);

        BasketDto basketAfterSubtraction = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);

        basketProductNameSet = basketAfterSubtraction.getProductNamesMap().keySet();
        //then
        assertThat(basketProductNameSet).containsExactlyInAnyOrderElementsOf(expectedProductNameSet);
        assertEquals(DB_PRODUCT1_GROSS_PRICE,basketAfterSubtraction.getTotalPrice());
    }

    @Test
    void tryToAddMoreOfProductThatIsInDb() {
        //when
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, DB_PRODUCT1_AMOUNT + 1);
        BasketDto updatedBasket = basketService.getBasketDtoByBasketId(DB_BASKET1_ID);
        //then
        assertEquals(DB_PRODUCT1_AMOUNT, updatedBasket.getProductNamesMap().get(DB_PRODUCT1_NAME));
    }

    @Test
    void fetchAllProductsFromBasket() {
        //given
        Set<String> expectedProductNamesSet = Set.of(DB_PRODUCT1_NAME, DB_PRODUCT2_NAME);
        basketService.addProductToBasket(DB_PRODUCT1_NAME, DB_BASKET1_ID, 1);
        basketService.addProductToBasket(DB_PRODUCT2_NAME, DB_BASKET1_ID, 1);
        //when
        Map<ProductDto, Integer> returnedProducts = basketService.fetchAllProductsFromBasket(DB_BASKET1_ID);
        Set<String> returnedProductNamesSet = returnedProducts.keySet().stream()
                .map(ProductDto::getName).collect(Collectors.toSet());
        //then
        assertThat(returnedProductNamesSet).containsExactlyInAnyOrderElementsOf(expectedProductNamesSet);
    }
}