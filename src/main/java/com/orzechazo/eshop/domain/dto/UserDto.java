package com.orzechazo.eshop.domain.dto;

import lombok.*;

import java.util.List;

@Value
@Builder
public class UserDto {

    String login;
    String password;
    List<String> orderIdList;
    List<ProductDto> favouriteProducts;
    BasketDto basket;

}
