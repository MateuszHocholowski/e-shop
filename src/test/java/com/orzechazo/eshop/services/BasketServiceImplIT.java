package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.BootstrapBasket;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BasketServiceImplIT {
    @Autowired
    private BasketRepository basketRepository;

    private BasketServiceImpl basketService;

    @BeforeEach
    void setUp() {
        BootstrapBasket bootstrapBasket = new BootstrapBasket(basketRepository);
        bootstrapBasket.loadData();

        basketService = new BasketServiceImpl(basketRepository);
    }

    @Test
    void getBasketByBasketId() {
        //when
        BasketDto returnedDto = basketService.getBasketDtoByBasketId("1");
        //then
        assertEquals("1",returnedDto.getBasketId());
        assertEquals(new BigDecimal("320"),returnedDto.getTotalPrice());
    }

    @Test
    void getBasketByBasketIdNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> basketService.getBasketDtoByBasketId("test"));
        assertEquals("Basket: test doesn't exist in database",exception.getMessage());
    }

    @Test
    void createBasket() {
        //given
        long count = basketRepository.count();
        BasketDto basketDto = BasketDto.builder().totalPrice(new BigDecimal("430")).build();
        //when
        BasketDto createdDto = basketService.createBasket(basketDto);
        //then
        assertEquals(new BigDecimal("430"),createdDto.getTotalPrice());
        assertNotNull(createdDto.getBasketId());
        assertEquals(count+1,basketRepository.count());
    }

    @Test
    void createBasketExistingId() {
        BasketDto basketDto = BasketDto.builder().basketId("1").build();
        Exception exception = assertThrows(BadRequestException.class,
                () -> basketService.createBasket(basketDto));
        assertEquals("Basket already has an id: 1",exception.getMessage());
    }

    @Test
    void updateBasket() {
        //given
        long count = basketRepository.count();
        BasketDto basketToUpdate = BasketDto.builder().basketId("1")
                .totalPrice(new BigDecimal("540")).build();
        //when
        BasketDto updatedBasket = basketService.updateBasket(basketToUpdate);
        //then
        assertEquals("1",updatedBasket.getBasketId());
        assertEquals(new BigDecimal("540"),updatedBasket.getTotalPrice());
        assertEquals(count,basketRepository.count());
    }

    @Test
    void updateBasketNotFound() {
        BasketDto basketDto = BasketDto.builder().basketId("test").build();
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> basketService.updateBasket(basketDto));
        assertEquals("Basket: test doesn't exist in database",exception.getMessage());
    }

    @Test
    void deleteBasket() {
        long count = basketRepository.count();
        //when
        basketService.deleteBasket("1");
        //then
        assertEquals(count-1,basketRepository.count());
    }
}