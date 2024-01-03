package com.orzechazo.eshop.domain.dto;

import lombok.*;
import lombok.experimental.NonFinal;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class BasketDto {

    Long basketId;
    List<ProductDto> products;
    @NonFinal
    UserDto user;
    BigDecimal totalPrice;
    public void setUser(UserDto user) {
        this.user = user;
    }
}
