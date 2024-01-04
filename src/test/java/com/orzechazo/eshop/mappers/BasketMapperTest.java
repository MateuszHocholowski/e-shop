package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.dto.BasketDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BasketMapperTest {

    BasketMapper mapper = BasketMapper.INSTANCE;
    @Test
    void basketDtoToBasket() {
        Basket expectedBasket = new Basket();
        expectedBasket.setBasketId(1L);
        expectedBasket.setProducts(new ArrayList<>());
        expectedBasket.setTotalPrice(new BigDecimal("12"));

        BasketDto basketDto = BasketDto.builder()
                .basketId(1L)
                .products(new ArrayList<>())
                .totalPrice(new BigDecimal("12"))
                .build();

        Basket mappedBasket = mapper.basketDtoToBasket(basketDto);

        assertEquals(expectedBasket,mappedBasket);
    }
    @Test
    void basketDtoToBasketNotEquals() {
        //given
        Basket expectedBasket = new Basket();
        expectedBasket.setBasketId(1L);
        expectedBasket.setProducts(new ArrayList<>());
        expectedBasket.setTotalPrice(new BigDecimal("12"));

        BasketDto basketDto = BasketDto.builder()
                .basketId(1L)
                .products(new ArrayList<>())
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
        basket.setBasketId(1L);
        basket.setProducts(new ArrayList<>());
        basket.setTotalPrice(new BigDecimal("12"));

        BasketDto expectedDto = BasketDto.builder()
                .basketId(1L)
                .products(new ArrayList<>())
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
        basket.setBasketId(1L);
        basket.setProducts(new ArrayList<>());
        basket.setTotalPrice(new BigDecimal("12"));

        BasketDto expectedDto = BasketDto.builder()
                .basketId(1L)
                .products(new ArrayList<>())
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