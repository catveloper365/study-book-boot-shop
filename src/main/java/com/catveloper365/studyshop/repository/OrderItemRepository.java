package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
