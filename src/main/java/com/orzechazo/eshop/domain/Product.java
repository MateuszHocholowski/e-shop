package com.orzechazo.eshop.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product extends BaseEntity {

    private String name;
    private BigDecimal netPrice;
    private BigDecimal grossPrice;
    @Lob
    private String description;
    private int amount;
    private byte[] image;
    @ManyToOne
    private Order order;
    @ManyToOne
    private User user;

}
