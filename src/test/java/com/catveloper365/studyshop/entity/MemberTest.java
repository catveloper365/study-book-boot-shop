package com.catveloper365.studyshop.entity;

import com.catveloper365.studyshop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("저장 시 Auditing")
    @WithMockUser(username = "save@test.com", roles = "USER")
    public void auditingSave(){
        //given
        Member member = new Member();
        memberRepository.save(member);

        em.flush();
        em.clear();

        //when
        Member savedMember = memberRepository.findById(member.getId())
                .orElseThrow(EntityNotFoundException::new);

        //then
        System.out.println("savedMember.getCreateBy() = " + savedMember.getCreatedBy());
        System.out.println("savedMember.getModifiedBy() = " + savedMember.getModifiedBy());
        System.out.println("savedMember.getRegTime() = " + savedMember.getRegTime());
        System.out.println("savedMember.getUpdateTime() = " + savedMember.getUpdateTime());

        assertThat(savedMember.getCreatedBy()).isNotEmpty();
        assertThat(savedMember.getModifiedBy()).isNotEmpty();
        assertThat(savedMember.getRegTime()).isNotNull();
        assertThat(savedMember.getUpdateTime()).isNotNull();
        assertThat(savedMember.getCreatedBy()).isEqualTo(member.getCreatedBy());
    }

    @Test
    @DisplayName("update 시 Auditing")
    @WithMockUser(username = "update@test.com", roles = "USER")
    public void auditingUpdate(){
        //given
        Member member = new Member();
        memberRepository.save(member);

        em.flush();
        em.clear();

        Member savedMember = memberRepository.findById(member.getId())
                .orElseThrow(EntityNotFoundException::new);

        //when
        savedMember.setName("hong");

        em.flush();
        em.clear();

        Member updatedMember = memberRepository.findById(member.getId())
                .orElseThrow(EntityNotFoundException::new);

        //then
        System.out.println("savedMember.getRegTime() = " + savedMember.getRegTime());
        System.out.println("savedMember.getUpdateTime() = " + savedMember.getUpdateTime());

        System.out.println("updatedMember.getRegTime() = " + updatedMember.getRegTime());
        System.out.println("updatedMember.getUpdateTime() = " + updatedMember.getUpdateTime());

        assertThat(savedMember.getRegTime()).isEqualTo(updatedMember.getRegTime());
        assertThat(savedMember.getUpdateTime()).isNotEqualTo(updatedMember.getUpdateTime());
    }

}