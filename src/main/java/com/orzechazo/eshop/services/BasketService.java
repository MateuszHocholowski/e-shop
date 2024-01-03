package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.ProductDto;
import com.orzechazo.eshop.domain.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface BasketService {
    BasketDto getBasketByBasketId(Long basketId);
    BasketDto getBasketByUser(UserDto userDto);
    BasketDto createBasket(UserDto userDto);
    BasketDto createBasket(BasketDto basketDto);
    BasketDto updateBasket(Long basketId, BasketDto basketDto);
    BasketDto updateBasket(UserDto userDto, BasketDto basketDto);
    void deleteBasket(Long basketId);
    void deleteBasketByUser(UserDto userDto);
}
