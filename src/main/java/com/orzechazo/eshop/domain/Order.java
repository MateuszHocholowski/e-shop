package com.orzechazo.eshop.domain;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Order extends BaseEntity{

    private List<Product> products;
    private LocalDate orderDate;
    private LocalDate admissionDate;
    private LocalDate paymentDate;
    private LocalDate realizationDate;
    private BigDecimal totalPrice;
}
