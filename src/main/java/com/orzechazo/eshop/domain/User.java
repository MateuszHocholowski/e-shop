package com.orzechazo.eshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "USERS")
public class User extends BaseEntity{

    @Column(unique = true)
    private String login;
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Order> orders = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Product> favouriteProducts;
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Basket basket;

    public void addOrder(Order order) {
            orders.add(order);
            order.setUser(this);
    }
}
