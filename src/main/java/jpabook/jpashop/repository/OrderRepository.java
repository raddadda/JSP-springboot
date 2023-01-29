package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        //동적 쿼리
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                //where는 조건문
                jpql += " where";
                isFirstCondition = false;
            } else {
                //and는 동일
                jpql += " and";
            }

            jpql += " o.status = :status";
        }

        //회원 이름 검색
        //해당 텍스트가 있을경우
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            //like는 해당하는게 포함되어있는지
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        //결과 반환
        return query.getResultList();

    }
     /*   return em.createQuery(jpql, Order.class)
                .setMaxResults(1000)
              .getResultList();
*/

        public List<Order> findAllWithMemberDelivery(){
           return em.createQuery(
                    "select o from Order o"+
                            //오더를 조회하는데 멤버와 딜리버리를 조인해서 한번에 가져오는
                            //lazy를 무시하고 진짜 데이터로 채워서 가져오는것이다.
                            " join fetch o.member m" +
                            " join fetch o.delivery d", Order.class)
                   .getResultList();
        }

        public List<Order> findAllWithItem(){
            return em.createQuery(
                    "select distinct o from Order o" +
                            " join fetch o.member m" +
                            " join fetch o.delivery d" +
                            //toone관계이기 때문에 계속 패치조인을 해도 된다. 데이터가 더 증가하지 않기 때문.
                            //여기까지는 기존과 같다.
                            " join fetch o.orderItems oi" +
                            " join fetch oi.item i", Order.class)
                    //distinct는 데이터 값이 완전 같아야하기때문에 데이터베이스에서는 효과가 거의 없다.
                    //그래서 jpa에서 자체적으로 사용한다. 그러면 같은 오더는 중복을 제거할 수 있다.
                            //조인을 되면서 DB에서 오더가 중복이 된다.
                    .setFirstResult(1)
                    .setMaxResults(100 )
                    .getResultList();


        }
    public List<Order> findAllWithMemberDelivery(int offset,int limit){
        return em.createQuery(
                        "select o from Order o"+
                                //오더를 조회하는데 멤버와 딜리버리를 조인해서 한번에 가져오는
                                //lazy를 무시하고 진짜 데이터로 채워서 가져오는것이다.
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
              //멤버와 딜리버리를 지워도 같은 결과지만 네트워크상 더 많은 과정이 생길 수 있다.
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
    }

