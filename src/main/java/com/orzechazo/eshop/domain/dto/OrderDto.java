package com.orzechazo.eshop.domain.dto;

import com.orzechazo.eshop.domain.Product;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Value
public class OrderDto {

    Long id;
    List<ProductDto> products;
    LocalDateTime orderDate;
    LocalDateTime admissionDate;
    LocalDateTime paymentDate;
    LocalDateTime realizationDate;
    BigDecimal totalPrice;
    UserDto user;
}
