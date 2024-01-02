package com.orzechazo.eshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class User extends BaseEntity{

    private String login;
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Order> orders;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Product> favouriteProducts;
    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    private Basket basket;

}
