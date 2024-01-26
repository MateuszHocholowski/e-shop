package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.BasketDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BasketMapperTest {

    private static final String BASKET_ID = "1";
    private static final String NAME_1 = "name1";
    private static final String NAME_2 = "name2";
    BasketMapper mapper = BasketMapper.INSTANCE;
    @Test
    void basketDtoToBasket() {
        Basket expectedBasket = new Basket();
        expectedBasket.setBasketId(BASKET_ID);
        expectedBasket.setTotalPrice(new BigDecimal("12"));

        BasketDto basketDto = BasketDto.builder()
                .basketId(BASKET_ID)
                .productNamesMap(new HashMap<>())
                .totalPrice(new BigDecimal("12"))
                .build();

        Basket mappedBasket = mapper.basketDtoToBasket(basketDto);

        assertEquals(expectedBasket,mappedBasket);
    }
    @Test
    void basketDtoToBasketNotEquals() {
        //given
        Basket expectedBasket = new Basket();
        expectedBasket.setBasketId(BASKET_ID);
        expectedBasket.setProducts(new HashMap<>());
        expectedBasket.setTotalPrice(new BigDecimal("12"));

        BasketDto basketDto = BasketDto.builder()
                .basketId(BASKET_ID)
                .productNamesMap(new HashMap<>())
                .totalPrice(new BigDecimal("13"))
                .build();
        //when
        Basket mappedBasket = mapper.basketDtoToBasket(basketDto);
        //then
        assertNotEquals(expectedBasket,mappedBasket);
    }

    @Test
    void basketToBasketDto() {
        //given
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        basket.setProducts(new HashMap<>());
        basket.setTotalPrice(new BigDecimal("12"));

        BasketDto expectedDto = BasketDto.builder()
                .basketId(BASKET_ID)
                .productNamesMap(new HashMap<>())
                .totalPrice(new BigDecimal("12"))
                .build();
        //when
        BasketDto mappedDto = mapper.basketToBasketDto(basket);
        //then
        assertEquals(expectedDto,mappedDto);
    }

    @Test
    void basketToBasketDtoNotEquals() {
        //given
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        basket.setProducts(new HashMap<>());
        basket.setTotalPrice(new BigDecimal("12"));

        BasketDto expectedDto = BasketDto.builder()
                .basketId(BASKET_ID)
                .productNamesMap(new HashMap<>())
                .totalPrice(new BigDecimal("13"))
                .build();
        //when
        BasketDto mappedDto = mapper.basketToBasketDto(basket);
        //then
        assertNotEquals(expectedDto,mappedDto);
    }
    @Test
    void basketDtoToBasketNull() {
        //when
        Basket mappedBasket = mapper.basketDtoToBasket(null);
        //then
        assertNull(mappedBasket);
    }
    @Test
    void basketToBasketDtoNull() {
        //when
        BasketDto mappedDto = mapper.basketToBasketDto(null);
        //then
        assertNull(mappedDto);
    }

    @Test
    void testMappingBasketProductsMapToBasketProductNamesMap() {
        //given
        Product product1 = new Product();
        product1.setName(NAME_1);
        Product product2 = new Product();
        product2.setName(NAME_2);

        Map<Product, Integer> basketProducts = Map.of(product1,1,product2,2);

        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        basket.setProducts(basketProducts);
        //when
        BasketDto mappedDto = mapper.basketToBasketDto(basket);
        //then
        assertEquals(1,mappedDto.getProductNamesMap().get(NAME_1));
        assertEquals(2,mappedDto.getProductNamesMap().get(NAME_2));
    }
}