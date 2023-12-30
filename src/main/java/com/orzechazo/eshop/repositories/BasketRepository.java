package com.orzechazo.eshop.repositories;

import com.orzechazo.eshop.domain.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {
}
