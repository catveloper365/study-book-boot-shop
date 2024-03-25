package com.catveloper365.studyshop.service;

import com.catveloper365.studyshop.constant.ItemSellStatus;
import com.catveloper365.studyshop.dto.OrderDto;
import com.catveloper365.studyshop.entity.Item;
import com.catveloper365.studyshop.entity.Member;
import com.catveloper365.studyshop.entity.Order;
import com.catveloper365.studyshop.entity.OrderItem;
import com.catveloper365.studyshop.repository.ItemRepository;
import com.catveloper365.studyshop.repository.MemberRepository;
import com.catveloper365.studyshop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired private OrderService orderService;

    @Autowired private OrderRepository orderRepository;

    @Autowired private ItemRepository itemRepository;

    @Autowired private MemberRepository memberRepository;

    public Item saveItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember() {
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("주문 테스트")
    public void order() {
        //given
        Item item = saveItem(); //상품 등록
        Member member = saveMember(); //회원가입

        //주문 정보
        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        //when
        Long orderId = orderService.order(orderDto, member.getEmail());

        //then
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
//        List<OrderItem> orderItems = order.getOrderItems();
        int totalPrice = orderDto.getCount() * item.getPrice();
        assertEquals(totalPrice, order.getTotalPrice());
    }
}