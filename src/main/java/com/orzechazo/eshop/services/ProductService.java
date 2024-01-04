package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getAllProducts();
    ProductDto getProductDtoByName(String productName);
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(String productName, ProductDto productDto);
    void deleteProductByProductName(String productName);
}
