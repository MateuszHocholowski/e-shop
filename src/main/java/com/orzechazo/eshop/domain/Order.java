package com.orzechazo.eshop.domain;

import com.orzechazo.eshop.exceptions.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Order extends BaseEntity{

    @Column(unique = true)
    private Long orderId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<Product> products;
    private LocalDateTime orderDate;
    private LocalDateTime admissionDate;
    private LocalDateTime paymentDate;
    private LocalDateTime realizationDate;
    private BigDecimal totalPrice;
    @ManyToOne
    private User user;
    @Transient
    private static long currentOrder = 0;

    public static void createOrderId(Order order) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        if(order.orderId == null) {
            String id = now.format(formatter) + currentOrder;
            currentOrder++;
            order.orderId = Long.parseLong(id);
        } else {
            throw new BadRequestException("Order already has an id: " + order.getOrderId());
        }
    }
}
