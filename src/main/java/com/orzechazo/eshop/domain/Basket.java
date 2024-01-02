package com.orzechazo.eshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Basket extends BaseEntity{

    @OneToMany(mappedBy = "basket")
    List<Product> products;
    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    User user;
    BigDecimal totalPrice;

}
