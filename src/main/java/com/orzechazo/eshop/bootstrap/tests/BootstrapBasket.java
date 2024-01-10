package com.orzechazo.eshop.bootstrap.tests;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.repositories.BasketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class BootstrapBasket {
    private final BasketRepository basketRepository;

    public BootstrapBasket(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }
    public void loadData() {
        setData();
    }
    private void setData(){
        log.debug("loading Baskets...");

        Basket basket1 = new Basket();
        Basket.createBasketId(basket1);
        basket1.setTotalPrice(new BigDecimal("120"));
        basketRepository.save(basket1);

        Basket basket2 = new Basket();
        Basket.createBasketId(basket2);
        basket2.setTotalPrice(new BigDecimal("210"));
        basketRepository.save(basket2);

        Basket basket3 = new Basket();
        basket3.setBasketId("1");
        basket3.setTotalPrice(new BigDecimal("320"));
        basketRepository.save(basket3);

        log.debug("Baskets loaded: " + basketRepository.count());

    }
}
