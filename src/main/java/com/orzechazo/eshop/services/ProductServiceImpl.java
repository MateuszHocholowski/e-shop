package com.orzechazo.eshop.services;

import com.orzechazo.eshop.mappers.ProductMapper;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


}
