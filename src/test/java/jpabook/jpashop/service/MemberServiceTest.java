package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;


//@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional

public class MemberServiceTest{

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;
    @Test
    @Rollback(false)
    //테스트시 DB를 유지시켜줌
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");
        //when
        Long savedId = memberService.join(member);
        //then
        //영속성 컨텍스트의 변경 내용을 DB에 반양하는 것을 말한다.
        em.flush();

        //두 객체의 값이 같은지 여부
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");
        //when
        memberService.join(member1);
        try{
            memberService.join(member2);
        } catch(IllegalStateException e){
            return;
        }
        memberService.join(member2);

        //then
        fail("예외가 발생해야 한다.");
        //fail로 오면 안되도록



        //then

    }}
