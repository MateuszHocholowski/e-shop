package com.orzechazo.eshop.domain.dto;

import jakarta.persistence.Lob;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Value
public class ProductDto {

    Long id;
    String name;
    BigDecimal netPrice;
    BigDecimal grossPrice;
    @Lob
    String description;
    int amount;
    byte[] image;
}
