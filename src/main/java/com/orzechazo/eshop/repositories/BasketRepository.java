package com.orzechazo.eshop.repositories;

import com.orzechazo.eshop.domain.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {

    Optional<Basket> findByBasketId(String basketId);
    void deleteByBasketId(String basketId);
}
