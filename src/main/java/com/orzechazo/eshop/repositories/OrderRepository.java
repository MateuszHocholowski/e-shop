package com.orzechazo.eshop.repositories;

import com.orzechazo.eshop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
