package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.mappers.ProductMapper;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public ProductDto getProductById(Long id) {
        Product returnedProduct = productRepository.findById(id).orElseThrow(RuntimeException::new);
        return productMapper.productToProductDto(returnedProduct);
    }

    @Override
    public ProductDto createProduct(ProductDto productDto){
        Product newProduct = productMapper.productDtoToProduct(productDto);
        if (productRepository.findByName(newProduct.getName()).isEmpty()) {
            return saveProductAndReturnDto(newProduct);
        } else {
            System.out.println("Product with that name is already in Database");
            return null;
        }
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product productToUpdate = productMapper.productDtoToProduct(productDto);
        productToUpdate.setId(id);
        return saveProductAndReturnDto(productToUpdate);
    }

    private ProductDto saveProductAndReturnDto(Product product) {
        return productMapper.productToProductDto(productRepository.save(product));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}
