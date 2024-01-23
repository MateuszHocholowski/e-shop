package com.orzechazo.eshop.domain;

import com.orzechazo.eshop.domain.helpers.IdCreator;
import com.orzechazo.eshop.exceptions.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity(name = "BASKETS")
public class Basket extends BaseEntity{

    @Column(unique = true)
    private String basketId;

    @ElementCollection
    @CollectionTable(name = "BASKET_PRODUCTS",
    joinColumns = @JoinColumn(name = "basket_id"))
    @MapKeyJoinColumn(name = "product_name")
    @Column(name = "amount")
    private Map<Product, Integer> products;

    private BigDecimal totalPrice;

    public static void createBasketId(Basket basket) {
        if (basket.basketId == null) {
            String basketId = IdCreator.createId(basket);
            basket.setBasketId(basketId);
        } else {
            throw new BadRequestException("Basket already has an id: " + basket.getBasketId());
        }
    }
}
