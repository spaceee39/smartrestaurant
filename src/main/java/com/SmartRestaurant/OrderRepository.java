package com.SmartRestaurant;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(Order.OrderStatus status);

    @Override
    @Cacheable("Orders")
    List<Order> findAllById(Iterable<Long> longs);
}
