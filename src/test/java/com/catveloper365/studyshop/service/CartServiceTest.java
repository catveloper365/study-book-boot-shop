package com.catveloper365.studyshop.service;

import com.catveloper365.studyshop.constant.ItemSellStatus;
import com.catveloper365.studyshop.dto.CartItemDto;
import com.catveloper365.studyshop.entity.CartItem;
import com.catveloper365.studyshop.entity.Item;
import com.catveloper365.studyshop.entity.Member;
import com.catveloper365.studyshop.repository.CartItemRepository;
import com.catveloper365.studyshop.repository.ItemRepository;
import com.catveloper365.studyshop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CartService cartService;

    @Autowired
    CartItemRepository cartItemRepository;

    Item saveItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    Member saveMember() {
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }

    CartItemDto createCartItemDto(Item item, int count) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(count);
        cartItemDto.setItemId(item.getId());
        return cartItemDto;
    }

    @Test
    @DisplayName("최초로 장바구니 담기")
    void addCart() {
        //given
        Item item = saveItem();
        Member member = saveMember();

        CartItemDto cartItemDto = createCartItemDto(item, 5);

        //when
        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());

        //then
        CartItem findCartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(item.getId(), findCartItem.getItem().getId());
        assertEquals(cartItemDto.getCount(), findCartItem.getCount());
    }

    @Test
    @DisplayName("중복 상품 장바구니 담기")
    void addDuplicateItemToCart() {
        //given
        Item item = saveItem();
        Member member = saveMember();

        //최초로 장바구니 담기
        CartItemDto cartItemDto = createCartItemDto(item, 5);
        Long cartItemId1 = cartService.addCart(cartItemDto, member.getEmail());

        //when
        Long cartItemId2 = cartService.addCart(cartItemDto, member.getEmail());

        //then
        assertEquals(cartItemId1, cartItemId2);

        CartItem findCartItem = cartItemRepository.findById(cartItemId1)
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(10, findCartItem.getCount());
    }

}