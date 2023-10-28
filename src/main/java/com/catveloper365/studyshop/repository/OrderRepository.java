package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
