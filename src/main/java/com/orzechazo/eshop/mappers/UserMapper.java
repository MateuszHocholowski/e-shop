package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "favouriteProducts", ignore = true)
    User userDtoToUser(UserDto userDto);

    @Mapping(target = "orderIdList", source = "orders", qualifiedByName = "ordersToOrderIdList")
    @Mapping(target = "password",ignore = true)
    @Mapping(target = "basket.productNamesMap", source = "basket.products", qualifiedByName = "productMapToProductNameMap")
    UserDto userToUserDto(User user);

    @Named("ordersToOrderIdList")
    static List<String> ordersToOrderIdList(List<Order> orders) {
        return orders.stream().map(Order::getOrderId).toList();
    }
    @Named("productMapToProductNameMap")
    static Map<String, Integer> productMapToProductNameMap(Map<Product, Integer> basketProducts) {
        return basketProducts.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey().getName(),entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
