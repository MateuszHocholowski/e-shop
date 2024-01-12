package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static  org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    public static final String ORDER_ID = "1";
    @InjectMocks
    private OrderController orderController;
    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;
    private final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void getAllOrders() throws Exception{
        List<OrderDto> orderDtos = List.of(OrderDto.builder().build(), OrderDto.builder().build());
        when(orderService.getAllOrders()).thenReturn(orderDtos);

        mockMvc.perform(get("/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    void getOrderByOrderId() throws Exception {
        OrderDto orderDto = OrderDto.builder().orderId(ORDER_ID).build();
        when(orderService.getOrderByOrderId(anyString())).thenReturn(orderDto);

        mockMvc.perform(get("/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId",equalTo(ORDER_ID)));
    }

    @Test
    void getOrdersByUser() throws Exception {
        List<OrderDto> orderDtos = List.of(OrderDto.builder().build(), OrderDto.builder().build());
        when(orderService.getOrdersByUser(any())).thenReturn(orderDtos);

        mockMvc.perform(get("/orders/user/testLogin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    void createOrder() throws Exception {
        OrderDto orderDto = OrderDto.builder().orderId(ORDER_ID).build();
        when(orderService.createOrder(any())).thenReturn(orderDto);

        mockMvc.perform(put("/orders/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId",equalTo(ORDER_ID)));
    }

    @Test
    void updateOrder() throws Exception {
        OrderDto orderDto = OrderDto.builder().orderId(ORDER_ID).build();
        when(orderService.updateOrder(any(),any())).thenReturn(orderDto);

        mockMvc.perform(post("/orders/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", equalTo(ORDER_ID)));
    }

    @Test
    void deleteOrderByOrderId() throws Exception {
        mockMvc.perform(delete("/orders/delete/orderId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(orderService,times(1)).deleteOrderByOrderId(anyString());
    }
}