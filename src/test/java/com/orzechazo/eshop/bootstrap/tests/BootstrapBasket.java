package com.orzechazo.eshop.bootstrap.tests;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.repositories.BasketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class BootstrapBasket {
    private final BasketRepository basketRepository;
    public static final String DB_BASKET_ID = "BASKET1";
    private List<Basket> baskets;

    public BootstrapBasket(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }
    public void loadData() {
        log.debug("loading Baskets...");

        Basket basket1 = new Basket();
        basket1.setBasketId(DB_BASKET_ID);
        basket1.setTotalPrice(new BigDecimal("120"));
        basketRepository.save(basket1);

        Basket basket2 = new Basket();
        Basket.createBasketId(basket2);
        basket2.setTotalPrice(new BigDecimal("210"));
        basketRepository.save(basket2);

        Basket basket3 = new Basket();
        Basket.createBasketId(basket3);
        basket3.setTotalPrice(new BigDecimal("320"));
        basketRepository.save(basket3);

        baskets = basketRepository.findAll();
    }

    public List<Basket> getBaskets() {
        return baskets;
    }
}