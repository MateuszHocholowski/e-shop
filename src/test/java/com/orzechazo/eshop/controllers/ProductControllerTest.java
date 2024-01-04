package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.ResourceAccessException;


import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static  org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    private ProductController productController;
    @Mock
    private ProductService productService;

    private MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void getAllProducts() throws Exception {
        List<ProductDto> productDtos = List.of(ProductDto.builder().build(), ProductDto.builder().build());
        when(productService.getAllProducts()).thenReturn(productDtos);

        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    void getProductByName() throws Exception{
        ProductDto productDto = ProductDto.builder().name("test").build();
        when(productService.getProductDtoByName(anyString())).thenReturn(productDto);

        mockMvc.perform(get("/products/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",equalTo("test")));
    }

    @Test
    void getProductByNameNotFound() throws Exception {
        when(productService.getProductDtoByName(anyString())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/products/wrongUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct() throws Exception{
        ProductDto productDto = ProductDto.builder().name("test").build();
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        when(productService.createProduct(any())).thenReturn(productDto);

        mockMvc.perform(put("/products/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",equalTo("test")));
    }

    @Test
    void updateProduct() throws Exception {
        ProductDto productDto = ProductDto.builder().name("test").build();
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        when(productService.updateProduct(anyString(), any())).thenReturn(productDto);

        mockMvc.perform(post("/products/update/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",equalTo("test")));
    }

    @Test
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/products/delete/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService,times(1)).deleteProductByProductName(anyString());
    }
}