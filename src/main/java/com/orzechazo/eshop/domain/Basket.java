package com.orzechazo.eshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Basket extends BaseEntity{

    @Column(unique = true)
    private Long basketId;
    @OneToMany(mappedBy = "basket")
    private List<Product> products;
    private BigDecimal totalPrice;
    @Transient
    private static long currentBasket = 0;

    public static void createBasketId(Basket basket) {
        LocalDate now = LocalDate.now();
        Random random = new Random();
        if (basket.getBasketId() == null) {
            String id = String.valueOf(currentBasket) + random.nextInt(100) + now.getYear()
                    + now.getMonthValue() + now.getDayOfMonth();
            currentBasket++;
            basket.setBasketId(Long.parseLong(id));
        }
    }
}
