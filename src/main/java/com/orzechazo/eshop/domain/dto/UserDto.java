package com.orzechazo.eshop.domain.dto;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.Product;
import lombok.*;

import java.util.List;

@Value
public class UserDto {

    Long id;
    String login;
    String password;
    List<OrderDto> orders;
    List<ProductDto> favouriteProducts;

}
