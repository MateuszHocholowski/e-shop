package com.orzechazo.eshop.domain.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class ProductDto {

    @NotEmpty
    String name;
    @PositiveOrZero
    BigDecimal netPrice;
    @PositiveOrZero
    BigDecimal grossPrice;
    @Lob
    String description;
    @PositiveOrZero
    int amount;
    byte[] image;
    BasketDto basket;
}
