package com.orzechazo.eshop.domain.dto;

import lombok.*;

import java.util.List;

@Value
@Builder
public class UserDto {

    String login;
    String password;
    List<OrderDto> orders;
    List<ProductDto> favouriteProducts;
    BasketDto basket;

}
