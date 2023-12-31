package com.orzechazo.eshop.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class BasketDto {

    List<ProductDto> products;
    UserDto user;
    BigDecimal totalPrice;
}
