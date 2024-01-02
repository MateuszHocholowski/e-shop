package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.ProductDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    ProductMapper mapper = ProductMapper.INSTANCE;

    @Test
    void productToProductDto() {
        //given
        Product product = new Product();
        product.setName("test");
        product.setAmount(5);
        product.setDescription("testDescription");
        product.setGrossPrice(new BigDecimal("2"));
        product.setNetPrice(new BigDecimal("1.5"));
        product.setId(1L);
        product.setImage(new byte[0]);
        //when
        ProductDto mappedDto = mapper.productToProductDto(product);
        //then
        assertNotNull(mappedDto);
        assertEquals("test",mappedDto.getName());
        assertEquals("testDescription",mappedDto.getDescription());
        assertEquals(5,mappedDto.getAmount());
        assertEquals(new BigDecimal("1.5"),mappedDto.getNetPrice());
        assertEquals(new BigDecimal("2"),mappedDto.getGrossPrice());
        assertNotNull(mappedDto.getImage());
    }

    @Test
    void productDtoToProduct() {
        //given
        ProductDto productDto = ProductDto.builder().name("test").amount(5).description("testDescription")
                .netPrice(new BigDecimal("1.5")).grossPrice(new BigDecimal("2")).image(new byte[0])
                .build();
        //when
        Product mappedProduct = mapper.productDtoToProduct(productDto);
        //then
        assertNotNull(mappedProduct);
        assertEquals("test",mappedProduct.getName());
        assertEquals("testDescription",mappedProduct.getDescription());
        assertEquals(5,mappedProduct.getAmount());
        assertEquals(new BigDecimal("1.5"),mappedProduct.getNetPrice());
        assertEquals(new BigDecimal("2"),mappedProduct.getGrossPrice());
        assertNotNull(mappedProduct.getImage());

    }

    @Test
    void productToProductDtoNull() {
        //when
        ProductDto mappedDto = mapper.productToProductDto(null);
        //then
        assertNull(mappedDto);
    }
    @Test
    void productDtoToProductNull() {
        //when
        Product mappedProduct = mapper.productDtoToProduct(null);
        //then
        assertNull(mappedProduct);
    }
}