package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.bootstrap.tests.BootstrapProduct;
import com.orzechazo.eshop.domain.Product;
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
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductControllerE2ETest {
    @Autowired
    private ProductRepository productRepository;
    MockMvc mockMvc;
    private int DB_DEFAULT_PRODUCTS_SIZE;
    private final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private final static String DB_PRODUCT_NAME = BootstrapProduct.DB_PRODUCT1_NAME;
    BootstrapProduct bootstrap;
    @BeforeEach
    void setUp() {
        bootstrap = new BootstrapProduct(productRepository);
        bootstrap.loadData();

        ProductServiceImpl productService = new ProductServiceImpl(productRepository);
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        DB_DEFAULT_PRODUCTS_SIZE = bootstrap.getProducts().size();
    }

    @Test
    void getAllProducts() throws Exception {
        List<String> DB_PRODUCT_NAMES_LIST = bootstrap.getProducts().stream().map(Product::getName).toList();
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(DB_DEFAULT_PRODUCTS_SIZE)))
                .andExpect(jsonPath("$[*].name").value(containsInAnyOrder(DB_PRODUCT_NAMES_LIST.toArray())));
    }

    @Test
    void getProductByName() throws Exception {
        mockMvc.perform(get("/products/" + DB_PRODUCT_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",equalTo(DB_PRODUCT_NAME)))
                .andExpect(jsonPath("$.amount",equalTo(5)))
                .andExpect(jsonPath("$.netPrice",is(new BigDecimal("1.5").doubleValue())))
                .andExpect(jsonPath("$.grossPrice",is(new BigDecimal("3.1").doubleValue())))
                .andExpect(jsonPath("$.description",equalTo("testDescription1")));
    }

    @Test
    void getProductByNameNotFound() throws Exception {
        mockMvc.perform(get("/products/productNotInDb"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Product: productNotInDb doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
    @Test
    void createProduct() throws Exception{
        ProductDto newProduct = ProductDto.builder().name("newProduct").amount(3)
                .netPrice(new BigDecimal("1.2")).build();
        mockMvc.perform(put("/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",equalTo("newProduct")))
                .andExpect(jsonPath("$.netPrice",is(new BigDecimal("1.2").doubleValue())))
                .andExpect(jsonPath("$.amount",equalTo(3)));

        assertEquals(DB_DEFAULT_PRODUCTS_SIZE +1,productRepository.count());
    }

    @Test
    void testCreateProductTwice() throws Exception {
        ProductDto newProduct = ProductDto.builder().name("newProduct").amount(3)
                .netPrice(new BigDecimal("1.2")).build();

        mockMvc.perform(put("/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(newProduct)))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(newProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Product: newProduct is already in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createProductWithNameAlreadyInDB() throws Exception {
        ProductDto productDto = ProductDto.builder().name(DB_PRODUCT_NAME).build();

        mockMvc.perform(put("/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Product: "+ DB_PRODUCT_NAME + " is already in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void updateProduct() throws Exception {
        ProductDto productToUpdate = ProductDto.builder().name(DB_PRODUCT_NAME)
                .amount(2).description("updatedDescription").build();

        mockMvc.perform(post("/products/update/" + productToUpdate.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(productToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",equalTo(DB_PRODUCT_NAME)))
                .andExpect(jsonPath("$.amount",equalTo(2)))
                .andExpect(jsonPath("$.description",equalTo("updatedDescription")));

        assertEquals(DB_DEFAULT_PRODUCTS_SIZE,productRepository.count());
    }

    @Test
    void updateProductNotFound() throws Exception {
        ProductDto productToUpdate = ProductDto.builder().name("productNotInDb").build();

        mockMvc.perform(post("/products/update/" + productToUpdate.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(productToUpdate))
                        .param("name","test"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Product: productNotInDb doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteProduct() throws Exception{
        mockMvc.perform(delete("/products/delete/" + DB_PRODUCT_NAME))
                .andExpect(status().isOk());

        assertEquals(DB_DEFAULT_PRODUCTS_SIZE -1,productRepository.count());
    }
}