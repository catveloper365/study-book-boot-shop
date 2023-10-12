package com.catveloper365.studyshop.service;

import com.catveloper365.studyshop.dto.MemberFormDto;
import com.catveloper365.studyshop.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    Member createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void join() {
        //given
        Member member = createMember();

        //when
        Member joinedMember = memberService.join(member);

        //then
        //기댓값 : 저장하려고 요청했던 값, 실제 값 : 실제 저장된 값
        assertEquals(member.getEmail(), joinedMember.getEmail());
        assertEquals(member.getName(), joinedMember.getName());
        assertEquals(member.getPassword(), joinedMember.getPassword());
        assertEquals(member.getAddress(), joinedMember.getAddress());
        assertEquals(member.getRole(), joinedMember.getRole());
    }
    
    @Test
    @DisplayName("중복 회원가입 테스트")
    public void duplicatedJoin(){
        //given
        Member member1 = createMember();
        Member member2 = createMember();

        //when
        Member joinedMember1 = memberService.join(member1);

        //then
        assertEquals(member1.getEmail(), joinedMember1.getEmail());
        Throwable e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertEquals("이미 가입된 회원입니다.", e.getMessage());
    }

}