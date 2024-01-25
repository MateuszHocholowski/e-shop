package com.orzechazo.eshop.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Value
@Builder
public class BasketDto {

    String basketId;
    Map<String, Integer> productNamesMap;
    BigDecimal totalPrice;
}
