package com.catveloper365.studyshop.service;

import com.catveloper365.studyshop.dto.CartItemDto;
import com.catveloper365.studyshop.entity.Cart;
import com.catveloper365.studyshop.entity.CartItem;
import com.catveloper365.studyshop.entity.Item;
import com.catveloper365.studyshop.entity.Member;
import com.catveloper365.studyshop.repository.CartItemRepository;
import com.catveloper365.studyshop.repository.CartRepository;
import com.catveloper365.studyshop.repository.ItemRepository;
import com.catveloper365.studyshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    /** 장바구니 담기 */
    public Long addCart(CartItemDto cartItemDto, String email) {
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(EntityNotFoundException::new);

        //로그인한 회원의 장바구니 엔티티 조회
        Cart cart = cartRepository.findByMemberId(member.getId())
                .orElseGet(() -> cartRepository.save(Cart.createCart(member)));

        Optional<CartItem> savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        if (savedCartItem.isPresent()) { //이미 장바구니에 있던 상품
            savedCartItem.get().addCount(cartItemDto.getCount()); //장바구니에서 수량만 증가
            return savedCartItem.get().getId();
        } else { //장바구니에 없던 상품
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
}
