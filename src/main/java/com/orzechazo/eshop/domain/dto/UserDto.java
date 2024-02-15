package com.orzechazo.eshop.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Value
@Builder
public class UserDto {

    @NotEmpty
    String login;
    @NotEmpty
    String password;
    List<String> orderIdList;
    List<ProductDto> favouriteProducts;
    BasketDto basket;

}
