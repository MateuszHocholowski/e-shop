package com.orzechazo.eshop.repositories;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {

    Optional<Basket> findByBasketId(Long basketId);
    void deleteByBasketId(Long basketId);
}
