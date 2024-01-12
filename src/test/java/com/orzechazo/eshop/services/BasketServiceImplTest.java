package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceImplTest {

    public static final String BASKET_ID = "1";
    @InjectMocks
    private BasketServiceImpl basketService;
    @Mock
    private BasketRepository basketRepository;
    @Test
    void getBasketByBasketId() {
        //given
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        //when
        BasketDto returnedDto = basketService.getBasketDtoByBasketId(BASKET_ID);
        //then
        assertEquals(BASKET_ID,returnedDto.getBasketId());
    }

    @Test
    void getBasketByBasketIdNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> basketService.getBasketDtoByBasketId(BASKET_ID));
        //then
        assertEquals("Basket: 1 doesn't exist in database",exception.getMessage());
    }

    @Test
    void createBasket() {
        //given
        BasketDto basketDto = BasketDto.builder().build();
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto createdDto = basketService.createBasket(basketDto);
        //then
        assertEquals(BASKET_ID,createdDto.getBasketId());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void createBasketNullId() {
        BasketDto basketDto = BasketDto.builder().basketId(BASKET_ID).build();
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);

        Exception exception = assertThrows(BadRequestException.class,
                () -> basketService.createBasket(basketDto));

        assertEquals("Basket already has an id: 1",exception.getMessage());
    }

    @Test
    void updateBasketById() {
        BasketDto basketDto = BasketDto.builder().basketId(BASKET_ID).build();
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto updatedDto = basketService.updateBasket(basketDto);
        //then
        assertEquals(BASKET_ID,updatedDto.getBasketId());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void deleteBasket() {
        //when
        basketService.deleteBasket(BASKET_ID);
        //then
        verify(basketRepository,times(1)).deleteByBasketId(any());
    }

}