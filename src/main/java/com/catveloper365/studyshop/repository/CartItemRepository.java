package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndItemId(Long cartId, Long itemId);
}
