package com.orzechazo.eshop.domain.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String name;
    private BigDecimal netPrice;
    private BigDecimal grossPrice;
    @Lob
    private String description;
    private int amount;
    private byte[] image;
}
