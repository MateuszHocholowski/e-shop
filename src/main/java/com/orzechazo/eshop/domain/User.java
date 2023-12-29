package com.orzechazo.eshop.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Order> orders;
    @OneToMany
    private List<Product> favouriteProducts;

}
