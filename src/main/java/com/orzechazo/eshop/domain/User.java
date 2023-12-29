package com.orzechazo.eshop.domain;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseEntity{

    private String login;
    private String password;
    private List<Order> orders;
    private List<Product> favouriteProducts;

}
