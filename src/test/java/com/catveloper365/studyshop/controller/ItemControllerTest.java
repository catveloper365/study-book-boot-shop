package com.catveloper365.studyshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("상품 등록 페이지 ADMIN 회원 접근")
    @WithMockUser(username = "admin", roles="ADMIN")
    public void itemForm() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"));

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상품 등록 페이지 일반 회원 접근")
    @WithMockUser(username = "user", roles = "USER")
    public void itemFormNotAdmin() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"));

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("상품 등록 페이지 미인증 사용자 접근")
    public void itemFormUnauthenticated() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"));

        //then
        resultActions.andDo(print())
                .andExpect(status().isUnauthorized());
    }

}