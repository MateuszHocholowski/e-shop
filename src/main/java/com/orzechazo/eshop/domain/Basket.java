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

    @Column(unique = true)
    private Long basketId;
    @OneToMany(mappedBy = "basket")
    private List<Product> products;
    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    private User user;
    private BigDecimal totalPrice;

}
