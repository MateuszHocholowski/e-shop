package com.orzechazo.eshop.controllers;

import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping({"","/"})
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }
    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProductByName(@PathVariable String name) {
        return productService.getProductDtoByName(name);
    }
    @PutMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return productService.createProduct(productDto);
    }
    @PostMapping("/update/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(@PathVariable String name,
                                    @RequestBody ProductDto productDto) {
        return productService.updateProduct(name, productDto);
    }
    @DeleteMapping("/delete/{name}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@PathVariable String name) {
        productService.deleteProductByProductName(name);
    }
}
