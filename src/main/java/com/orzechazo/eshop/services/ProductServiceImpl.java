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
    public ProductDto getProductDtoByName(String productName) {
        return productMapper.productToProductDto(getProductByName(productName));
    }
    private Product getProductByName(String productName) {
        return productRepository.findByName(productName)
                .orElseThrow(() -> new ResourceNotFoundException("Product: " + productName
                        + " doesn't exist in database."));
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
    public ProductDto updateProduct(String productName, ProductDto productDto) {
        Product currentProduct = getProductByName(productName);
        Product productToUpdate = productMapper.productDtoToProduct(productDto);
        if (productToUpdate.getName() == null) {
            throw new BadRequestException("Please insert the name of the Product you want to update");
        }
        productToUpdate.setId(currentProduct.getId());
        return saveProductAndReturnDto(productToUpdate);
    }

    private ProductDto saveProductAndReturnDto(Product product) {
        return productMapper.productToProductDto(productRepository.save(product));
    }

    @Override
    public void deleteProductByProductName(String productName) {
        productRepository.deleteByName(productName);
    }
}
