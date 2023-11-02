package com.catveloper365.studyshop.entity;

import com.catveloper365.studyshop.constant.ItemSellStatus;
import com.catveloper365.studyshop.repository.ItemRepository;
import com.catveloper365.studyshop.repository.OrderItemRepository;
import com.catveloper365.studyshop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @PersistenceContext
    EntityManager em;

    Item createItem(int num) {
        Item item = new Item();
        item.setItemNm("테스트 상품" + num);
        item.setPrice(10000);
        item.setItemDetail("상세 설명" + num);
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return item;
    }

    Order createOrder(int itemCnt) {
        Order order = new Order();

        for (int i = 1; i <= itemCnt; i++) {
            Item item = this.createItem(i);
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);

            //orderItem은 영속성 컨텍스트에 저장X
            order.getOrderItems().add(orderItem);
        }
        return order;
    }

    @Test
    @DisplayName("저장 영속성 전이")
    void cascadeSave(){
        //given
        Order order = this.createOrder(3);

        //order 엔티티 저장 & 강제 flush를 통해 DB 반영
        orderRepository.saveAndFlush(order);
        em.clear(); //영속성 컨텍스트의 상태 초기화

        //when
        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);

        //then
        assertEquals(3, savedOrder.getOrderItems().size());
    }
    @Test
    @DisplayName("삭제 영속성 전이")
    void cascadeRemove(){
        //given
        Order order = this.createOrder(3);
        orderRepository.save(order);

        //when
        orderRepository.delete(order);
        em.flush();
        em.clear();

        //then
        assertThrows(EntityNotFoundException.class, ()->
            orderRepository.findById(order.getId())
                    .orElseThrow(EntityNotFoundException::new));
    }

    @Test
    @DisplayName("고아객체 제거")
    void orphanRemoval(){
        //given
        Order order = this.createOrder(3);
        orderRepository.save(order);

        //when
        order.getOrderItems().remove(0);
        em.flush();
        em.clear();

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);

        //then
        assertEquals(2, savedOrder.getOrderItems().size());
        assertEquals("테스트 상품2", savedOrder.getOrderItems().get(0).getItem().getItemNm());
    }

    @Test
    @DisplayName("fetch 전략")
    void fetchType(){
        //given
        Order order = this.createOrder(2);
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);

        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        //when
        OrderItem savedOrderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("Order class : " + savedOrderItem.getOrder().getClass());
        System.out.println("==============");
        savedOrderItem.getOrder().getOrderDate();
        System.out.println("==============");

        //then
    }

}