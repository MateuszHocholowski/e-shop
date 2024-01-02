package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.mappers.ProductMapper;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::productToProductDto)
                .toList();
    }

    @Override
    public ProductDto getProductByProductId(Long productId) {
        Product returnedProduct = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + productId
                        + " doesn't exist in database."));
        return productMapper.productToProductDto(returnedProduct);
    }

    @Override
    public ProductDto createProduct(ProductDto productDto){
        Product newProduct = productMapper.productDtoToProduct(productDto);
        if (productRepository.findByName(newProduct.getName()).isEmpty()) {
            return saveProductAndReturnDto(newProduct);
        } else {
            throw new BadRequestException("Product: " + productDto.getName() + " is already in database.");
        }
    }

    @Override
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product productToUpdate = productMapper.productDtoToProduct(productDto);
        productToUpdate.setProductId(productId);
        return saveProductAndReturnDto(productToUpdate);
    }

    private ProductDto saveProductAndReturnDto(Product product) {
        return productMapper.productToProductDto(productRepository.save(product));
    }

    @Override
    public void deleteProductByProductId(Long id) {
        productRepository.deleteByProductId(id);
    }
}
