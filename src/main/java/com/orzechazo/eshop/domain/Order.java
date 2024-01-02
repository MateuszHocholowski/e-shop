package com.orzechazo.eshop.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class Order extends BaseEntity{

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<Product> products;
    private LocalDateTime orderDate;
    private LocalDateTime admissionDate;
    private LocalDateTime paymentDate;
    private LocalDateTime realizationDate;
    private BigDecimal totalPrice;
    @ManyToOne
    private User user;
}
