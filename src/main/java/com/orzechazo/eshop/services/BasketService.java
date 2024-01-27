package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.BasketDto;

public interface BasketService {
    BasketDto getBasketDtoByBasketId(String basketId);
    BasketDto createBasket(BasketDto basketDto);
    BasketDto updateBasket(BasketDto basketDto);
    void deleteBasket(String basketId);
    BasketDto addProductToBasket(String productName, String basketId, int amount);
    BasketDto addProductToBasket(String productName, String basketId);
    BasketDto subtractProductFromBasket(String productName, String basketId, int amount);
    BasketDto subtractProductFromBasket(String productName, String basketId);
    BasketDto removeProductFromBasket(String productName, String basketId);
}

