package com.orzechazo.eshop.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class OrderDto {

    String orderId;
    List<ProductDto> products;
    LocalDateTime orderDate;
    LocalDateTime admissionDate;
    LocalDateTime paymentDate;
    LocalDateTime realizationDate;
    BigDecimal totalPrice;
    UserDto user;
}
