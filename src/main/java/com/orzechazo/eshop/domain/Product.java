package com.orzechazo.eshop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "PRODUCTS")
public class Product extends BaseEntity {

    @Column(name = "PRODUCT_NAME", unique = true)
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
    @ManyToOne
    private Basket basket;

}
