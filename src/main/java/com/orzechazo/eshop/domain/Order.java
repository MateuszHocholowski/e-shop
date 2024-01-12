package com.orzechazo.eshop.domain;

import com.orzechazo.eshop.domain.helpers.IdCreator;
import com.orzechazo.eshop.exceptions.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = "user")
@Entity(name = "ORDERS")
public class Order extends BaseEntity{

    @Column(unique = true)
    private String orderId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<Product> products;
    private LocalDateTime orderDate;
    private LocalDateTime admissionDate;
    private LocalDateTime paymentDate;
    private LocalDateTime realizationDate;
    private BigDecimal totalPrice;
    @ManyToOne
    private User user;

    public static void createOrderId(Order order) {
        if(order.orderId == null) {
            String orderId = IdCreator.createId(order);
            order.setOrderId(orderId);
        } else {
            throw new BadRequestException("Order already has an id: " + order.getOrderId());
        }
    }
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            if (user.getOrders() == null) {
                user.setOrders(new ArrayList<>());
            }
            if (!user.getOrders().contains(this)) {
                user.getOrders().add(this);
            }
        } //todo change the method or change connection between user and order
    }
}
