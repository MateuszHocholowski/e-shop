package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Test
    void getAllProducts() {
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setName("test1");
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setName("test2");
        List<Product> products = List.of(product1,product2);
        when(productRepository.findAll()).thenReturn(products);
        //when
        List<ProductDto> returnedDtos = productService.getAllProducts();
        //then
        assertEquals(2,returnedDtos.size());
        assertEquals(1L,returnedDtos.get(0).getProductId());
        assertEquals("test2",returnedDtos.get(1).getName());
    }

    @Test
    void getProductByProductId() {
        //given
        Product product = new Product();
        product.setProductId(4L);
        product.setAmount(6);
        when(productRepository.findByProductId(anyLong())).thenReturn(Optional.of(product));
        //when
        ProductDto returnedDto = productService.getProductByProductId(4L);
        //then
        assertEquals(6,returnedDto.getAmount());
        assertEquals(4L,returnedDto.getProductId());
    }
    @Test
    void getProductByProductIdBadRequest() {
        //when
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductByProductId(4L));
        //then
        assertEquals("Product with id: 4 doesn't exist in database.",exception.getMessage());
    }

    @Test
    void createProduct() {
        //given
        ProductDto productDto = ProductDto.builder().name("testName").build();
        Product product = new Product();
        product.setProductId(1L);
        product.setName("testName");
        when(productRepository.save(any())).thenReturn(product);
        //when
        ProductDto returnedDto = productService.createProduct(productDto);
        //then
        assertEquals("testName",returnedDto.getName());
        assertEquals(1L,returnedDto.getProductId());
        verify(productRepository,times(1)).save(any());
    }

    @Test
    void createProductExists() {
        //given
        ProductDto productDto = ProductDto.builder().name("testName").build();
        when(productRepository.findByName(any())).thenReturn(Optional.of(new Product()));
        //when
        Exception exception = assertThrows(BadRequestException.class, () -> productService.createProduct(productDto));
        //then
        assertEquals("Product: testName is already in database.",exception.getMessage());
    }

    @Test
    void updateProduct() {
        //given
        ProductDto productDto = ProductDto.builder().name("testName").build();
        Product product = new Product();
        product.setId(1L);
        product.setName("testName");
        when(productRepository.save(any())).thenReturn(product);
        //when
        ProductDto returnedDto = productService.updateProduct(1L, productDto);
        //then
        assertEquals("testName",returnedDto.getName());
        verify(productRepository,times(1)).save(any());
    }

    @Test
    void deleteProduct() {
        //when
        productService.deleteProductByProductId(1L);
        //then
        verify(productRepository,times(1)).deleteByProductId(any());
    }
}