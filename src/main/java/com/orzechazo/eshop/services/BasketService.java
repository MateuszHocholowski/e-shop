package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.BasketDto;

public interface BasketService {
    BasketDto getBasketByBasketId(Long basketId);
    BasketDto createBasket(BasketDto basketDto);
    BasketDto updateBasket(BasketDto basketDto);
    void deleteBasket(Long basketId);
}
