package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.Bootstrap;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.ProductRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.orzechazo.eshop.bootstrap.tests.Bootstrap.DB_PRODUCT1_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductServiceImplIT {
    @Autowired
    private BasketRepository basketRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    private ProductService productService;
    private int DEFAULT_DB_PRODUCT_COUNT;
    private Bootstrap bootstrap;

    @BeforeEach()
    void setUp() {
        bootstrap = new Bootstrap(orderRepository,userRepository,productRepository,basketRepository);
        bootstrap.loadData();

        productService = new ProductServiceImpl(productRepository);
        DEFAULT_DB_PRODUCT_COUNT = bootstrap.getProducts().size();
    }

    @Test
    void getAllProducts() {
        //given
        List<String> dbProductNamesList = bootstrap.getProducts().stream()
                .map(Product::getName).toList();
        //when
        List<ProductDto> productDtos = productService.getAllProducts();
        List<String> productNames = productDtos.stream().map(ProductDto::getName).toList();
        //then
        assertThat(productNames).containsExactlyInAnyOrderElementsOf(dbProductNamesList);
    }

    @Test
    void getProductByProductName() {
        //given
        ProductDto expectedDto = ProductDto.builder().name(DB_PRODUCT1_NAME).amount(5)
                .netPrice(new BigDecimal("1.5")).grossPrice(new BigDecimal("3.1"))
                .description("testDescription1").build();
        //when
        ProductDto returnedDto = productService.getProductDtoByName(DB_PRODUCT1_NAME);
        //then
        assertEquals(expectedDto,returnedDto);
    }

    @Test
    void getProductByProductNameThatIsNotInDatabase() {
        //when
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductDtoByName("productNotExisting"));
        //then
        assertEquals("Product: productNotExisting doesn't exist in database.",exception.getMessage());
    }
    @Test
    void testCreateNewProduct() {
        ProductDto newProduct = ProductDto.builder().name("newProduct").amount(2)
                .netPrice(new BigDecimal("1")).grossPrice(new BigDecimal("2"))
                .description("testDescription").build();
        //when
        ProductDto createdDto = productService.createProduct(newProduct);
        //then
        assertEquals(newProduct,createdDto);
        assertEquals(DEFAULT_DB_PRODUCT_COUNT +1, productRepository.count());
    }

    @Test
    void testTryToCreateProductWhoseNameIsAlreadyInDatabase() {
        ProductDto existingProduct = ProductDto.builder().name(DB_PRODUCT1_NAME).amount(2)
                .netPrice(new BigDecimal("13")).grossPrice(new BigDecimal("7"))
                .build();
        Exception exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(existingProduct));
        assertEquals("Product: " + DB_PRODUCT1_NAME + " is already in database.",exception.getMessage());
    }

    @Test
    void testTryToCreateTheSameProductTwice() {
        ProductDto newProduct = ProductDto.builder().name("newProduct").build();
        productService.createProduct(newProduct);
        Exception exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(newProduct));
        assertEquals("Product: newProduct is already in database.",exception.getMessage());
    }

    @Test
    void testUpdateProduct() {
        ProductDto productToUpdate = ProductDto.builder().name(DB_PRODUCT1_NAME).amount(10)
                .netPrice(new BigDecimal("2")).grossPrice(new BigDecimal("4"))
                .description("newDescription").build();

        productService.updateProduct(DB_PRODUCT1_NAME, productToUpdate);

        ProductDto updatedProduct = productService.getProductDtoByName(DB_PRODUCT1_NAME);

        assertEquals(new BigDecimal("2"),updatedProduct.getNetPrice());
        assertEquals(new BigDecimal("4"),updatedProduct.getGrossPrice());
        assertEquals(10,updatedProduct.getAmount());
        assertEquals("newDescription",updatedProduct.getDescription());
        assertEquals(DEFAULT_DB_PRODUCT_COUNT,productRepository.count());
    }

    @Test
    void testUpdateProductNoName() {
        ProductDto productDtoWithoutNameField = ProductDto.builder().amount(10)
                .netPrice(new BigDecimal("2")).grossPrice(new BigDecimal("4"))
                .description("newDescription").build();

        Exception exception = assertThrows(BadRequestException.class,
                () -> productService.updateProduct(DB_PRODUCT1_NAME,productDtoWithoutNameField));
        assertEquals("Please insert the name of the Product you want to update", exception.getMessage());
    }

    @Test
    void testUpdateProductDifferentName() {
        //given
        ProductDto productToUpdate = ProductDto.builder().name("newProductName").amount(10)
                .netPrice(new BigDecimal("2")).grossPrice(new BigDecimal("4"))
                .description("newDescription").build();
        //when
        productService.updateProduct(DB_PRODUCT1_NAME,productToUpdate);
        //then
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductDtoByName(DB_PRODUCT1_NAME));
        assertEquals("Product: "+ DB_PRODUCT1_NAME + " doesn't exist in database.",exception.getMessage());
    }

    @Test
    void testDeleteProduct() {
        //todo change deleteProduct method to catch DataIntegrityViolationException
    }
}
