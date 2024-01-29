package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.ProductDto;

import java.util.Map;

public interface BasketService {
    BasketDto getBasketDtoByBasketId(String basketId);
    BasketDto createBasket(BasketDto basketDto);
    BasketDto updateBasket(BasketDto basketDto);
    void deleteBasket(String basketId);
    BasketDto addProductToBasket(String productName, String basketId, int amount);
    BasketDto subtractProductFromBasket(String productName, String basketId, int amount);
    BasketDto removeProductFromBasket(String productName, String basketId);
    Map<ProductDto, Integer> fetchAllProductsFromBasket(String basketId);
}

