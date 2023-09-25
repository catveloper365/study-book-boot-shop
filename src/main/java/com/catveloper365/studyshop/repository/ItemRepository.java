package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemNm(String itemNm); //상품명으로 조회

    //상품명 혹은 상품 상세 설명으로 조회
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    //파라미터 값보다 가격이 작은 상품 조회(오름차순)
    List<Item> findByPriceLessThan(Integer price);

    //파라미터 값보다 가격이 작은 상품 조회(내림차순)
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
}
