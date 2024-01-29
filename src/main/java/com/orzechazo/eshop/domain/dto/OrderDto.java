package com.orzechazo.eshop.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.orzechazo.eshop.domain.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class OrderDto {

    String orderId;
    List<ProductDto> products;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime orderDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime admissionDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime paymentDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime realizationDate;
    OrderStatus orderStatus;
    BigDecimal totalPrice;
    String userLogin;
}
