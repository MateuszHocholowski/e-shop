package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.BasketDto;

public interface BasketService {
    BasketDto getBasketByBasketId(String basketId);
    BasketDto createBasket(BasketDto basketDto);
    BasketDto updateBasket(BasketDto basketDto);
    void deleteBasket(String basketId);
}
