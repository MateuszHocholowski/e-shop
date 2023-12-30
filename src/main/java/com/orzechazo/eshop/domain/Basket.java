package com.orzechazo.eshop.domain;

import jakarta.persistence.*;
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
@Entity
public class Basket extends BaseEntity{

    @OneToMany(mappedBy = "basket")
    List<Product> products;
    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    User user;
    BigDecimal totalPrice;

}
