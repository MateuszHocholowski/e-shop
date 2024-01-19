package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "favouriteProducts", ignore = true)
    @Mapping(target = "basket", ignore = true)
    User userDtoToUser(UserDto userDto);

    @Mapping(target = "orderIdList", source = "orders", qualifiedByName = "ordersToOrderIdList")
    @Mapping(target = "password",ignore = true)
    UserDto userToUserDto(User user);

    @Named("ordersToOrderIdList")
    static List<String> ordersToOrderIdList(List<Order> orders) {
        return orders.stream().map(Order::getOrderId).toList();
    }
}
