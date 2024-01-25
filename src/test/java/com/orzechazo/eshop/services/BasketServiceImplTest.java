package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceImplTest {

    public static final String BASKET_ID = "1";
    private static final String PRODUCT_NAME = "productName";
    @InjectMocks
    private BasketServiceImpl basketService;
    @Mock
    private BasketRepository basketRepository;
    @Mock
    private ProductService productService;
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

        ProductDto newProductDto = ProductDto.builder().name(PRODUCT_NAME).build();
        int amountToAdd = 2;

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>());
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,amountToAdd)));

        when(productService.getProductDtoByName(anyString())).thenReturn(newProductDto);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.addProductToBasket(PRODUCT_NAME, BASKET_ID, amountToAdd);
        //then
        assertEquals(amountToAdd, updatedDto.getProducts().get(newProductDto));
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void addNewProductToBasketWithoutAmount() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);

        ProductDto newProductDto = ProductDto.builder().name(PRODUCT_NAME).build();
        Basket basket = new Basket();
        basket.setProducts(new HashMap<>());
        basket.setBasketId(BASKET_ID);
        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,1)));
        when(productService.getProductDtoByName(anyString())).thenReturn(newProductDto);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.addProductToBasket(PRODUCT_NAME, BASKET_ID);
        //then
        assertEquals(1, updatedDto.getProducts().get(newProductDto));
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void addProductThatIsAlreadyInBasket() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);
        int productAmountInBasket = 3;
        ProductDto newProductDto = ProductDto.builder().name(PRODUCT_NAME).build();

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket)));
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket + 1)));

        when(productService.getProductDtoByName(anyString())).thenReturn(newProductDto);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.addProductToBasket(PRODUCT_NAME, BASKET_ID);
        //then
        assertEquals(productAmountInBasket + 1, updatedDto.getProducts().get(newProductDto));
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void subtractProductFromBasket() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);
        int productAmountInBasket = 3;
        ProductDto newProductDto = ProductDto.builder().name(PRODUCT_NAME).build();
        int amountToSubtract = 2;

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket)));
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket - amountToSubtract)));

        when(productService.getProductDtoByName(anyString())).thenReturn(newProductDto);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.subtractProductFromBasket(PRODUCT_NAME,BASKET_ID,amountToSubtract);
        //then
        assertEquals(productAmountInBasket-amountToSubtract,updatedDto.getProducts().get(newProductDto));
        verify(basketRepository, times(1)).save(any());
    }
    @Test
    void subtractProductFromBasketWithoutAmount() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);
        int productAmountInBasket = 3;
        ProductDto newProductDto = ProductDto.builder().name(PRODUCT_NAME).build();

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket)));
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket - 1)));

        when(productService.getProductDtoByName(anyString())).thenReturn(newProductDto);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.subtractProductFromBasket(PRODUCT_NAME,BASKET_ID);
        //then
        assertEquals(productAmountInBasket-1,updatedDto.getProducts().get(newProductDto));
        verify(basketRepository, times(1)).save(any());
    }

    @Test
    void removeProductFromBasket() {
        //given
        Product newProduct = new Product();
        newProduct.setName(PRODUCT_NAME);
        int productAmountInBasket = 1;
        ProductDto newProductDto = ProductDto.builder().name(PRODUCT_NAME).build();

        Basket basket = new Basket();
        basket.setProducts(new HashMap<>(Map.of(newProduct,productAmountInBasket)));
        basket.setBasketId(BASKET_ID);

        Basket updatedBasket = new Basket();
        updatedBasket.setProducts(new HashMap<>());

        when(productService.getProductDtoByName(anyString())).thenReturn(newProductDto);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(updatedBasket);
        //when
        BasketDto updatedDto = basketService.subtractProductFromBasket(PRODUCT_NAME, BASKET_ID);
        //then
        assertFalse(updatedDto.getProducts().containsKey(newProductDto));
        verify(basketRepository,times(1)).save(any());
    }
}