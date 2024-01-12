package com.orzechazo.eshop.controllers;

import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }
    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto getOrderByOrderId(@PathVariable String orderId) {
        return orderService.getOrderByOrderId(orderId);
    }
    @GetMapping("/user/{login}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getOrdersByUser(@PathVariable String login) {
        return orderService.getOrdersByUser(login);
    }
    @PutMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@RequestBody OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }
    @PostMapping("/update/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto updateOrder(@PathVariable String orderId, @RequestBody OrderDto orderDto) {
        return orderService.updateOrder(orderId,orderDto);
    }
    @DeleteMapping("/delete/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrderByOrderId(@PathVariable String orderId) {
        orderService.deleteOrderByOrderId(orderId);
    }
}
