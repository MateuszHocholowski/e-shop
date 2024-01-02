package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.mappers.ProductMapper;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private final ProductMapper productMapper = ProductMapper.INSTANCE;
    @Test
    void getAllProducts() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("test1");
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("test2");
        List<Product> products = List.of(product1,product2);
        when(productRepository.findAll()).thenReturn(products);
        //when
        List<ProductDto> returnedDtos = productService.getAllProducts();
        //then
        assertEquals(2,returnedDtos.size());
        assertEquals(1L,returnedDtos.get(0).getId());
        assertEquals("test2",returnedDtos.get(1).getName());
    }

    @Test
    void getProductById() {
        //given
        Product product = new Product();
        product.setId(4L);
        product.setAmount(6);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        //when
        ProductDto returnedDto = productService.getProductById(4L);
        //then
        assertEquals(4L,returnedDto.getId());
        assertEquals(6,returnedDto.getAmount());
    }

    @Test
    void createProduct() {
        //given
        ProductDto productDto = ProductDto.builder().id(1L).name("testName").build();
        Product product = new Product();
        product.setId(1L);
        product.setName("testName");
        when(productRepository.save(any())).thenReturn(product);
        //when
        ProductDto returnedDto = productService.createProduct(productDto);
        //then
        assertEquals(1L,returnedDto.getId());
        assertEquals("testName",returnedDto.getName());
        verify(productRepository,times(1)).save(any());
    }

    @Test
    void createProductExists() {
        //given
        ProductDto productDto = ProductDto.builder().id(1L).name("testName").build();
        when(productRepository.findByName(any())).thenReturn(new Product());
        //when
        ProductDto returnedDto = productService.createProduct(productDto);
        //then
        assertNull(returnedDto);
        verify(productRepository,times(0)).save(any());
    }

    @Test
    void updateProduct() {
        //given
        ProductDto productDto = ProductDto.builder().id(1L).name("testName").build();
        Product product = new Product();
        product.setId(1L);
        product.setName("testName");
        when(productRepository.save(any())).thenReturn(product);
        //when
        ProductDto returnedDto = productService.updateProduct(1L, productDto);
        //then
        assertEquals(1L,returnedDto.getId());
        assertEquals("testName",returnedDto.getName());
        verify(productRepository,times(1)).save(any());
    }

    @Test
    void deleteProduct() {
        //when
        productService.deleteProductById(1L);
        //then
        verify(productRepository,times(1)).deleteById(any());
    }
}