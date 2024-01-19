package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.bootstrap.tests.BootstrapBasket;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.services.BasketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BasketControllerE2ETest {

    @Autowired
    BasketRepository basketRepository;
    BasketServiceImpl basketService;
    BootstrapBasket bootstrap;
    MockMvc mockMvc;
    ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private int DB_DEFAULT_BASKET_COUNT;
    private static final String DB_BASKET_ID = BootstrapBasket.DB_BASKET_ID;
    @BeforeEach
    void setUp() {
        basketService = new BasketServiceImpl(basketRepository);
        bootstrap = new BootstrapBasket(basketRepository);
        bootstrap.loadData();
        BasketController basketController = new BasketController(basketService);
        mockMvc = MockMvcBuilders.standaloneSetup(basketController).build();

        DB_DEFAULT_BASKET_COUNT = bootstrap.getBaskets().size();
    }

    @Test
    void getBasketByBasketId() throws Exception {
        mockMvc.perform(get("/baskets/" + DB_BASKET_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId",equalTo(DB_BASKET_ID)))
                .andExpect(jsonPath("$.totalPrice",equalTo(new BigDecimal("120").intValue())));
    }

    @Test
    void getBasketByBasketIdThatIsNotInDB() throws Exception {
        mockMvc.perform(get("/baskets/basketNotInDb")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Basket: basketNotInDb doesn't exist in database",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createBasket() throws Exception {
        BasketDto newBasket = BasketDto.builder().totalPrice(new BigDecimal("2.5")).build();
        mockMvc.perform(put("/baskets/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(newBasket)));

        assertEquals(DB_DEFAULT_BASKET_COUNT+1, basketRepository.count());
    }

    @Test
    void updateBasket() throws Exception {
        BasketDto basketToUpdate = BasketDto.builder().basketId(DB_BASKET_ID)
                .totalPrice(new BigDecimal("7")).build();
        mockMvc.perform(post("/baskets/update/" + DB_BASKET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(basketToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId",equalTo(DB_BASKET_ID)))
                .andExpect(jsonPath("$.totalPrice",equalTo(new BigDecimal("7").intValue())));

        assertEquals(DB_DEFAULT_BASKET_COUNT,basketRepository.count());
    }

    @Test
    void testUpdateBasketWithIdNotInDB() throws Exception {
        BasketDto basketToUpdate = BasketDto.builder().basketId("basketNotInDb").build();

        mockMvc.perform(post("/baskets/update/basketNotInDb")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(basketToUpdate)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Basket: basketNotInDb doesn't exist in database",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteBasket() throws Exception {
        mockMvc.perform(delete("/baskets/delete/" + DB_BASKET_ID))
                .andExpect(status().isOk());

        assertEquals(DB_DEFAULT_BASKET_COUNT-1, basketRepository.count());
    }
}