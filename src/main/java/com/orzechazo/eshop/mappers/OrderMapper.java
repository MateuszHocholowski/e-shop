package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    @Mapping(target = "user.password",ignore = true)
    Order orderDtoToOrder(OrderDto orderDto);
    @Mapping(target = "user.password",ignore = true)
    OrderDto orderToOrderDto(Order order);
}
