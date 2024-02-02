package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    @Mapping(target = "products", ignore = true)
    Order orderDtoToOrder(OrderDto orderDto);
    @Mapping(target = "userLogin",source = "user.login")
    @Mapping(target = "productNamesMap", source = "products", qualifiedByName = "productMapToProductNameMap")
    OrderDto orderToOrderDto(Order order);

    @Named("productMapToProductNameMap")
    static Map<String, Integer> productMapToProductNameMap(Map<Product, Integer> orderProducts) {
        return orderProducts.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey().getName(),entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
