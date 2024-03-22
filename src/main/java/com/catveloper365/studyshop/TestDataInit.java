package com.catveloper365.studyshop;

import com.catveloper365.studyshop.constant.Role;
import com.catveloper365.studyshop.dto.MemberFormDto;
import com.catveloper365.studyshop.entity.Member;
import com.catveloper365.studyshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /*
    테스트용 데이터 생성
     */
    @PostConstruct
    public void init() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("a@test.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("행복시 행복동");
        memberFormDto.setPassword("12345678");
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        member.setRole(Role.ADMIN); //ADMIN 권한으로 생성
        memberRepository.save(member);
    }
}
