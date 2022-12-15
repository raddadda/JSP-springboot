package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
//엔티티에 의해 생성된 DB에 접근하는 메서드들을 사용하기 위한 인터페이스다.

@RequiredArgsConstructor
//생성자 주입 방법
public class MemberRepository {
    private final EntityManager em;
//엔티티매니저를 통해 엔티티를 저장하거나, 조회할 때 엔티티매니저는 영속 컨텍스트에 엔티티를 보관하고 관리한다.
    public void save(Member member) {
        //jpa에서 영속성은 엔티티를 영구적을 저장해주는 환경을 의미한다.
        em.persist(member);
    }
    public Member findOne(Long id){
        return em.find(Member.class,id);
    }
    public List<Member> findAll() {
        //엔티티 객체인 member의 모든 데이터를 추출하는 코드
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name",Member.class)
                .setParameter("name", name)
                .getResultList();
    }

    }
