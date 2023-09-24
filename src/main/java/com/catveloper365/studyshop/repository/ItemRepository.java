package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
