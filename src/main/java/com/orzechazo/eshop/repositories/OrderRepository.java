package com.orzechazo.eshop.repositories;

import com.orzechazo.eshop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(Long aLong);
}
