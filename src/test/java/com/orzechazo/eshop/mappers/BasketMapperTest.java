package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.dto.BasketDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BasketMapperTest {

    public static final String BASKET_ID = "1";
    BasketMapper mapper = BasketMapper.INSTANCE;
    @Test
    void basketDtoToBasket() {
        Basket expectedBasket = new Basket();
        expectedBasket.setBasketId(BASKET_ID);
        expectedBasket.setProducts(new HashMap<>());
        expectedBasket.setTotalPrice(new BigDecimal("12"));

        BasketDto basketDto = BasketDto.builder()
                .basketId(BASKET_ID)
                .products(new HashMap<>())
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
                .products(new HashMap<>())
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
                .products(new HashMap<>())
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
                .products(new HashMap<>())
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
}