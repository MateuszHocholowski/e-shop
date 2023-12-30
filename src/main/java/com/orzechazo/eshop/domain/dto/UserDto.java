package com.orzechazo.eshop.domain.dto;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String login;
    private String password;
    private List<OrderDto> orders;
    private List<ProductDto> favouriteProducts;

}
