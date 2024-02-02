package com.orzechazo.eshop.domain.dto;

import jakarta.persistence.Lob;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class ProductDto {

    String name;
    BigDecimal netPrice;
    BigDecimal grossPrice;
    @Lob
    String description;
    int amount;
    byte[] image;
}
