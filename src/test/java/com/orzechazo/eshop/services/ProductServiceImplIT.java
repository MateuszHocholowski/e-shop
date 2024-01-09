package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.BootstrapProduct;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductServiceImplIT {

    @Autowired
    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach()
    void setUp() {
        BootstrapProduct bootstrapProduct = new BootstrapProduct(productRepository);
        bootstrapProduct.loadData();

        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void getAllProducts() {
        long productCount = productRepository.count();
        List<ProductDto> productDtos = productService.getAllProducts();

        assertEquals(productCount,productDtos.size());
        assertEquals("product1", productDtos.get(0).getName());
        assertEquals(7,productDtos.get(1).getAmount());
        assertEquals(new BigDecimal("5.7"),productDtos.get(2).getNetPrice());
    }

    @Test
    void getProductByProductName() {
        //given
        ProductDto expectedDto = ProductDto.builder().name("product3").amount(12)
                .netPrice(new BigDecimal("5.7")).grossPrice(new BigDecimal("2.2"))
                .description("testDescription3").build();
        //when
        ProductDto returnedDto = productService.getProductDtoByName("product3");
        //then
        assertEquals(expectedDto,returnedDto);
    }

    @Test
    void getProductByProductNameNotFound() {
        //when
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductDtoByName("testName"));
        //then
        assertEquals("Product: testName doesn't exist in database.",exception.getMessage());
    }
    @Test
    void testCreateProduct() {
        ProductDto newProduct = ProductDto.builder().name("test").amount(2)
                .netPrice(new BigDecimal("1")).grossPrice(new BigDecimal("2"))
                .description("testDescription").build();
        //when
        ProductDto createdDto = productService.createProduct(newProduct);
        //then
        assertEquals(newProduct,createdDto);
    }

    @Test
    void testCreateProductExists() {
        ProductDto newProduct = ProductDto.builder().name("product1").amount(2)
                .netPrice(new BigDecimal("1")).grossPrice(new BigDecimal("2"))
                .description("testDescription").build();
        Exception exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(newProduct));
        assertEquals("Product: product1 is already in database.",exception.getMessage());
    }

    @Test
    void testUpdateProduct() {
        ProductDto productToUpdate = ProductDto.builder().name("product2").amount(10)
                .netPrice(new BigDecimal("2")).grossPrice(new BigDecimal("4"))
                .description("newDescription").build();

        ProductDto updatedProduct = productService
                .updateProduct(productToUpdate.getName(), productToUpdate);
        ProductDto expectedProduct = productService.getProductDtoByName(productToUpdate.getName());

        assertEquals(updatedProduct,productToUpdate);
        assertEquals(expectedProduct,productToUpdate);
    }

    @Test
    void testUpdateProductNoName() {
        ProductDto productToUpdate = ProductDto.builder().amount(10)
                .netPrice(new BigDecimal("2")).grossPrice(new BigDecimal("4"))
                .description("newDescription").build();
        ProductDto updatedProduct1 = productService.
                updateProduct("product1",productToUpdate);
        ProductDto expectedProduct = productService.getProductDtoByName("product1");

        assertEquals(expectedProduct,updatedProduct1);
    }

    @Test
    void testUpdateProductDifferentName() {
        //given
        ProductDto productToUpdate = ProductDto.builder().name("product4").amount(10)
                .netPrice(new BigDecimal("2")).grossPrice(new BigDecimal("4"))
                .description("newDescription").build();
        //when
        productService.updateProduct("product1",productToUpdate);
        //then
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductDtoByName("product1"));
        assertEquals("Product: product1 doesn't exist in database.",exception.getMessage());
    }

    @Test
    void testDeleteProduct() {
        long productCount = productRepository.count();
        //when
        productService.deleteProductByProductName("product1");
        //then
        assertEquals(productCount - 1,productService.getAllProducts().size());
    }
}
