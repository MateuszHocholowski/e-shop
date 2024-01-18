package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.services.BasketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static  org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BasketControllerTest {

    public static final String BASKET_ID = "1";
    @InjectMocks
    private BasketController basketController;
    @Mock
    private BasketService basketService;
    MockMvc mockMvc;
    private final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(basketController).build();
    }
    @Test
    void getBasketByBasketId() throws Exception{
        BasketDto basketDto = BasketDto.builder().basketId(BASKET_ID).build();
        when(basketService.getBasketDtoByBasketId(any())).thenReturn(basketDto);

        mockMvc.perform(get("/baskets/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId",equalTo(BASKET_ID)));
    }

    @Test
    void createBasket() throws Exception{
        BasketDto basketDto = BasketDto.builder().build();
        when(basketService.createBasket(any())).thenReturn(basketDto);

        mockMvc.perform(put("/baskets/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(basketDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$",notNullValue()));
    }

    @Test
    void updateBasket() throws Exception {
        BasketDto basketDto = BasketDto.builder().basketId(BASKET_ID).build();
        when(basketService.updateBasket(any())).thenReturn(basketDto);

        mockMvc.perform(post("/baskets/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(basketDto))
                        .param("basketId",BASKET_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId",equalTo(BASKET_ID)));
    }

    @Test
    void deleteBasket() throws Exception {
        mockMvc.perform(delete("/baskets/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(basketService,times(1)).deleteBasket(anyString());
    }
}