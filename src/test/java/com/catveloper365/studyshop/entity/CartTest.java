package com.catveloper365.studyshop.entity;

import com.catveloper365.studyshop.dto.MemberFormDto;
import com.catveloper365.studyshop.repository.CartRepository;
import com.catveloper365.studyshop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    Member createMember() {
        MemberFormDto dto = new MemberFormDto();
        dto.setEmail("test@email.com");
        dto.setName("홍길동");
        dto.setAddress("서울시 마포구 합정동");
        dto.setPassword("1234");
        return Member.createMember(dto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 맵핑 조회")
    public void findCartAndMember(){
        //given
        Member member = this.createMember();
        memberRepository.save(member); //회원 엔티티 영속성 컨텍스트에 저장

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart); //장바구니 엔티티 영속성 컨텍스트에 저장

        //when
        em.flush(); //영속성 컨텍스트에 저장된 엔티티를 DB에 강제 반영
        em.clear(); //영속성 컨텍스트를 비워 DB에서 조회하도록 함

        Cart savedCart = cartRepository.findById(cart.getId())
                .orElseThrow(EntityNotFoundException::new);

        //then
        assertEquals(savedCart.getMember().getId(), member.getId());
    }

}