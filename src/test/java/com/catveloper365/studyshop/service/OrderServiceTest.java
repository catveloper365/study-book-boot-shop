package com.catveloper365.studyshop.service;

import com.catveloper365.studyshop.constant.ItemSellStatus;
import com.catveloper365.studyshop.constant.OrderStatus;
import com.catveloper365.studyshop.dto.OrderDto;
import com.catveloper365.studyshop.entity.Item;
import com.catveloper365.studyshop.entity.Member;
import com.catveloper365.studyshop.entity.Order;
import com.catveloper365.studyshop.exception.OutOfStockException;
import com.catveloper365.studyshop.repository.ItemRepository;
import com.catveloper365.studyshop.repository.MemberRepository;
import com.catveloper365.studyshop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

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

    private static OrderDto createOrderDto(Item item, int orderCount) {
        OrderDto orderDto = new OrderDto();
        orderDto.setCount(orderCount);
        orderDto.setItemId(item.getId());
        return orderDto;
    }

    @Test
    @DisplayName("정상 주문")
    void order() {
        //given
        Item item = saveItem(); //상품 등록(영속 상태)
        Member member = saveMember(); //회원가입

        int initStock = item.getStockNumber(); //주문 전 재고 수량

        //주문 정보
        OrderDto orderDto = createOrderDto(item, 10);

        //when
        Long orderId = orderService.order(orderDto, member.getEmail());

        //then
        //case1. 총 주문 금액을 통해 검증
        int totalPrice = orderDto.getCount() * item.getPrice();

        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(totalPrice, findOrder.getTotalPrice());

        //case2. 주문 수량만큼 재고가 감소했는 지를 통해 검증
        Item findItem = itemRepository.findById(item.getId())
                .orElseThrow(EntityNotFoundException::new);

        int restStock = initStock - orderDto.getCount(); //주문 후 재고 수량
        assertEquals(restStock, findItem.getStockNumber());

        //case3. 주문 상태를 통해 검증
        assertEquals(OrderStatus.ORDER, findOrder.getOrderStatus());
    }

    @Test
    @DisplayName("재고 부족")
    void outOfStock() {
        //given
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = createOrderDto(item, item.getStockNumber() + 1);

        //when, then
        Throwable e = assertThrows(OutOfStockException.class, () -> orderService.order(orderDto, member.getEmail()));

        //전체 메세지 : 상품의 재고가 부족합니다. (현재 재고 수량: " + this.stockNumber + ")
        String basicMsg = e.getMessage().substring(0, e.getMessage().lastIndexOf("."));
        assertEquals("상품의 재고가 부족합니다", basicMsg);
    }

    @Test
    @DisplayName("품절")
    void soldOut() {
        //given
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = createOrderDto(item, item.getStockNumber());

        //when
        Long orderId = orderService.order(orderDto, member.getEmail());

        //then
        Item findItem = itemRepository.findById(item.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(0, findItem.getStockNumber());
        assertEquals(ItemSellStatus.SOLD_OUT, findItem.getItemSellStatus());
    }

    @Test
    @DisplayName("주문 취소")
    void cancelOrder() {
        //given
        Item item = saveItem();
        Member member = saveMember();

        int initStockNumber = item.getStockNumber();

        //재고 수량 만큼 모두 주문
        OrderDto orderDto = createOrderDto(item, initStockNumber);
        Long orderId = orderService.order(orderDto, member.getEmail());

        //when
        orderService.cancelOrder(orderId);

        //then
        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        //주문 취소를 하면 주문 상태가 취소로 변경됨
        assertEquals(OrderStatus.CANCEL, findOrder.getOrderStatus());
        //주문 수량 만큼 재고 수량이 다시 증가함
        assertEquals(initStockNumber, item.getStockNumber());
        //품절 상태인 상품을 주문 취소하면 상품 판매 상태가 판매중으로 변경됨
        assertEquals(ItemSellStatus.SELL, item.getItemSellStatus());
    }
}