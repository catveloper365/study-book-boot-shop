package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item> {

    List<Item> findByItemNm(String itemNm); //상품명으로 조회

    //상품명 혹은 상품 상세 설명으로 조회
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    //파라미터 값보다 가격이 작은 상품 조회(가격 오름차순)
    List<Item> findByPriceLessThan(Integer price);

    //파라미터 값보다 가격이 작은 상품 조회(가격 내림차순)
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    //검색어를 상품 상세 설명에 포함하고 있는 상품 & 가격 내림차순 조회
    //파라미터 맵핑 방법으로 @Param 어노테이션 사용
    @Query("select i from Item i where i.itemDetail like " +
            "%:itemDetail% order by i.price desc")
    List<Item> findByItemDetailByParam(@Param("itemDetail") String itemDetail);


    //파라미터 맵핑 방법으로 ?파라미터 번호를 사용
    @Query("select i from Item i where i.itemDetail like " +
            "%?1% order by i.price desc")
    List<Item> findByItemDetailByOrder(String itemDetail);

    @Query(value="select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}
