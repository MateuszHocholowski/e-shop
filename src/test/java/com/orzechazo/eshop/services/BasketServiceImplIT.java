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

import static com.orzechazo.eshop.bootstrap.tests.BootstrapBasket.DB_BASKET1_TOTAL_PRICE;
import static com.orzechazo.eshop.bootstrap.tests.BootstrapBasket.DB_BASKET1_ID;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BasketServiceImplIT {

    @Autowired
    private BasketRepository basketRepository;
    private BasketServiceImpl basketService;
    private int DB_DEFAULT_BASKET_COUNT;

    @BeforeEach
    void setUp() {
        BootstrapBasket bootstrapBasket = new BootstrapBasket(basketRepository);
        bootstrapBasket.loadData();

        basketService = new BasketServiceImpl(basketRepository);
        DB_DEFAULT_BASKET_COUNT = bootstrapBasket.getBaskets().size();
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
}