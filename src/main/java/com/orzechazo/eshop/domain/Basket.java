package com.orzechazo.eshop.domain;

import com.orzechazo.eshop.domain.helpers.IdCreator;
import com.orzechazo.eshop.exceptions.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Basket extends BaseEntity{

    @Column(unique = true)
    private String basketId;
    @OneToMany(mappedBy = "basket")
    private List<Product> products;
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
