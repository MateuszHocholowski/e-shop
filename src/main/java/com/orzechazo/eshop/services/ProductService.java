package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getAllProducts();
    ProductDto getProductById(Long id);
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(Long id, ProductDto productDto);
    ProductDto saveProductAndReturnDto(Product product);
    void deleteProductById(Long id);
}
