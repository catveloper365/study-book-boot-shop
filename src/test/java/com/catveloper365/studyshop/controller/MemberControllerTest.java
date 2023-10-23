package com.catveloper365.studyshop.controller;

import com.catveloper365.studyshop.dto.MemberFormDto;
import com.catveloper365.studyshop.entity.Member;
import com.catveloper365.studyshop.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(String email, String password) {
        MemberFormDto dto = new MemberFormDto();
        dto.setEmail(email);
        dto.setName("홍길동");
        dto.setAddress("서울시 마포구 합정동");
        dto.setPassword(password);
        Member member = Member.createMember(dto, passwordEncoder);
        return memberService.join(member);
    }

    @Test
    @DisplayName("로그인/로그아웃 성공")
    public void loginLogoutSuccess() throws Exception {
        //given
        String email = "test@email.com";
        String password = "1234";
        this.createMember(email, password);

        //when
        ResultActions resultLogin = mockMvc.perform(formLogin()
                .userParameter("email")
                .loginProcessingUrl("/members/login")
                .user(email).password(password));

        //then
        resultLogin.andExpect(SecurityMockMvcResultMatchers.authenticated());

        //when
        ResultActions resultLogout = mockMvc.perform(SecurityMockMvcRequestBuilders.logout("/members/logout"));

        //then
        resultLogout.andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @Test
    @DisplayName("로그인 실패")
    public void loginFail() throws Exception {
        //given
        String email = "test@email.com";
        String password = "1234";
        this.createMember(email, password);

        //when
        ResultActions resultLogin = mockMvc.perform(formLogin()
                .userParameter("email")
                .loginProcessingUrl("/members/login")
                .user(email).password("12345"));

        //then
        resultLogin.andExpect(SecurityMockMvcResultMatchers.unauthenticated());

        //when
        //로그인에 실패하면 /members/login/error GET 요청이 발생
        ResultActions resultError = mockMvc.perform(MockMvcRequestBuilders.get("/members/login/error"));

        //then
        resultError.andExpect(MockMvcResultMatchers.model().attribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요"));
    }

}