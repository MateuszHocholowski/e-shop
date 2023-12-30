package com.orzechazo.eshop.repositories;

import com.orzechazo.eshop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
