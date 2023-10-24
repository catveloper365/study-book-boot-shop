package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
