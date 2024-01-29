package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceImplTest {

    public static final String BASKET_ID = "1";
    private static final String PRODUCT_NAME = "productName";
    private static final BigDecimal GROSS_PRICE = new BigDecimal("12");
    private static final String PRODUCT2_NAME = "product2";
    @InjectMocks
    private BasketServiceImpl basketService;
    @Mock
    private BasketRepository basketRepository;
    @Mock
    private ProductRepository productRepository;
    @Test
    void getBasketByBasketId() {
        //given
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        //when
        BasketDto returnedDto = basketService.getBasketDtoByBasketId(BASKET_ID);
        //then
        assertEquals(BASKET_ID,returnedDto.getBasketId());
    }

    @Test
    void getBasketByBasketIdNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> basketService.getBasketDtoByBasketId(BASKET_ID));
        //then
        assertEquals("Basket: 1 doesn't exist in database",exception.getMessage());
    }

    @Test
    void createBasket() {
        //given
        BasketDto basketDto = BasketDto.builder().build();
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto createdDto = basketService.createBasket(basketDto);
        //then
        assertEquals(BASKET_ID,createdDto.getBasketId());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void createBasketNullId() {
        BasketDto basketDto = BasketDto.builder().basketId(BASKET_ID).build();
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);

        Exception exception = assertThrows(BadRequestException.class,
                () -> basketService.createBasket(basketDto));

        assertEquals("Basket already has an id: 1",exception.getMessage());
    }

    @Test
    void updateBasketById() {
        BasketDto basketDto = BasketDto.builder().basketId(BASKET_ID).build();
        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto updatedDto = basketService.updateBasket(basketDto);
        //then
        assertEquals(BASKET_ID,updatedDto.getBasketId());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void deleteBasket() {
        //when
        basketService.deleteBasket(BASKET_ID);
        //then
        verify(basketRepository,times(1)).deleteByBasketId(any());
    }

    @Test
    void addNewProductToBasket() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);

        int amountToAdd = 2;

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>());
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,amountToAdd)));

        when(productRepository.findByName(anyString())).thenReturn(Optional.of(newProduct));
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.addProductToBasket(PRODUCT_NAME, BASKET_ID, amountToAdd);
        //then
        assertEquals(amountToAdd, updatedDto.getProductNamesMap().get(PRODUCT_NAME));
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void addNewProductToBasketWithoutAmount() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>());
        basket.setBasketId(BASKET_ID);
        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,1)));
        when(productRepository.findByName(anyString())).thenReturn(Optional.of(newProduct));
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.addProductToBasket(PRODUCT_NAME, BASKET_ID, 1);
        //then
        assertEquals(1, updatedDto.getProductNamesMap().get(PRODUCT_NAME));
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void addProductThatIsAlreadyInBasket() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);
        int productAmountInBasket = 3;

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket)));
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket + 1)));

        when(productRepository.findByName(anyString())).thenReturn(Optional.of(newProduct));
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.addProductToBasket(PRODUCT_NAME, BASKET_ID, 1);
        //then
        assertEquals(productAmountInBasket + 1, updatedDto.getProductNamesMap().get(PRODUCT_NAME));
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void subtractProductFromBasket() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);
        int productAmountInBasket = 3;
        int amountToSubtract = 2;

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket)));
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket - amountToSubtract)));

        when(productRepository.findByName(anyString())).thenReturn(Optional.of(newProduct));
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.subtractProductFromBasket(PRODUCT_NAME,BASKET_ID,amountToSubtract);
        //then
        assertEquals(productAmountInBasket-amountToSubtract,updatedDto.getProductNamesMap().get(PRODUCT_NAME));
        verify(basketRepository, times(1)).save(any());
    }
    @Test
    void subtractProductFromBasketWithoutAmount() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);
        int productAmountInBasket = 3;

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket)));
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket - 1)));

        when(productRepository.findByName(anyString())).thenReturn(Optional.of(newProduct));
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.subtractProductFromBasket(PRODUCT_NAME,BASKET_ID, 1);
        //then
        assertEquals(productAmountInBasket-1,updatedDto.getProductNamesMap().get(PRODUCT_NAME));
        verify(basketRepository, times(1)).save(any());
    }

    @Test
    void removeProductFromBasket() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);
        int productAmountInBasket = 1;

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket)));
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>());

        when(productRepository.findByName(anyString())).thenReturn(Optional.of(newProduct));
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.removeProductFromBasket(PRODUCT_NAME, BASKET_ID);
        //then
        assertFalse(updatedDto.getProductNamesMap().containsKey(PRODUCT_NAME));
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void fetchAllProductsFromBasket() {
        //given
        Product product1 = new Product();
        product1.setName(PRODUCT_NAME);

        Product product2 = new Product();
        product2.setName(PRODUCT2_NAME);

        Basket basketInDb = new Basket();
        basketInDb.setBasketId(BASKET_ID);
        basketInDb.setProducts(Map.of(product1, 1, product2, 2));
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basketInDb));
        //when
        Map<ProductDto, Integer> returnedProductMap = basketService.fetchAllProductsFromBasket(BASKET_ID);
        List<String> returnedProductNameList = returnedProductMap.keySet().stream()
                .map(ProductDto::getName).toList();
        //then
        assertThat(returnedProductNameList)
                .containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_NAME, PRODUCT2_NAME));
    }

    @Test
    void testCountBasketTotalPrice() {
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);
        newProduct.setGrossPrice(GROSS_PRICE);

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>());
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,3)));
        updatedBasket.setTotalPrice(GROSS_PRICE.multiply(new BigDecimal("3")));

        when(productRepository.findByName(anyString())).thenReturn(Optional.of(newProduct));
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedBasketDto = basketService.addProductToBasket(PRODUCT_NAME,BASKET_ID,3);
        //then
        assertEquals(GROSS_PRICE.multiply(new BigDecimal("3")), updatedBasketDto.getTotalPrice());
    }
}