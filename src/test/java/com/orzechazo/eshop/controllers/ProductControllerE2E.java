package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.bootstrap.tests.BootstrapProduct;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.ProductRepository;
import com.orzechazo.eshop.services.ProductServiceImpl;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductControllerE2ETests {
    @Autowired
    private ProductRepository productRepository;
    MockMvc mockMvc;
    private int REPO_SIZE;
    private final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    @BeforeEach
    void setUp() {
        BootstrapProduct bootstrapProduct = new BootstrapProduct(productRepository);
        bootstrapProduct.loadData();

        ProductServiceImpl productService = new ProductServiceImpl(productRepository);
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        REPO_SIZE = (int) productRepository.count();
    }

    @Test
    void getAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(REPO_SIZE)));
    }

    @Test
    void getProductByName() throws Exception {
        mockMvc.perform(get("/products/product1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount",equalTo(5)))
                .andExpect(jsonPath("$.netPrice",is(new BigDecimal("1.5").doubleValue())))
                .andExpect(jsonPath("$.grossPrice",is(new BigDecimal("3.1").doubleValue())))
                .andExpect(jsonPath("$.description",equalTo("testDescription1")));
    }

    @Test
    void getProductByNameNotFound() throws Exception {
        mockMvc.perform(get("/products/test"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Product: test doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
    @Test
    void createProduct() throws Exception{
        ProductDto productDto = ProductDto.builder().name("testName").amount(3)
                .netPrice(new BigDecimal("1.2")).build();
        mockMvc.perform(put("/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",equalTo("testName")))
                .andExpect(jsonPath("$.netPrice",is(new BigDecimal("1.2").doubleValue())))
                .andExpect(jsonPath("$.amount",equalTo(3)))
                .andExpect(result -> assertEquals(REPO_SIZE+1,productRepository.count()));
    }

    @Test
    void createProductExistingName() throws Exception {
        ProductDto productDto = ProductDto.builder().name("product2").build();

        mockMvc.perform(put("/products/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Product: product2 is already in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void updateProduct() throws Exception {
        ProductDto productToUpdate = ProductDto.builder().name("product3")
                .amount(2).description("updatedDescription").build();

        mockMvc.perform(post("/products/update/" + productToUpdate.getName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(productToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",equalTo("product3")))
                .andExpect(jsonPath("$.amount",equalTo(2)))
                .andExpect(jsonPath("$.description",equalTo("updatedDescription")))
                .andExpect(result -> assertEquals(REPO_SIZE,productRepository.count()));
    }

    @Test
    void updateProductNotFound() throws Exception {
        ProductDto productToUpdate = ProductDto.builder().name("test").build();

        mockMvc.perform(post("/products/update/" + productToUpdate.getName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(productToUpdate))
                .param("name","test"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Product: test doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteProduct() throws Exception{
        mockMvc.perform(delete("/products/delete/product2"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(REPO_SIZE-1,productRepository.count()));
    }
}