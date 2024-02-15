package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.dto.BasketDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface BasketMapper {

    BasketMapper INSTANCE = Mappers.getMapper(BasketMapper.class);

    @Mapping(target = "products", ignore = true)
    Basket basketDtoToBasket(BasketDto basketDto);

    @Mapping(target = "productNamesMap", source = "products", qualifiedByName = "productMapToProductNameMap")
    BasketDto basketToBasketDto(Basket basket);

    @Named("productMapToProductNameMap")
    static Map<String, Integer> productMapToProductNameMap(Map<Product, Integer> basketProducts) {
        return basketProducts.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey().getName(),entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
