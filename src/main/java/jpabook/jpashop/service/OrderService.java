package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;

import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
        //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);
        //배송정보 생성
        //케스테이드?
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);
        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);
        //이 외의 형태로 다른사람이 만드는것을 방지하기 위해 PROTECTED를 이용한다.

        //주문 저장
        orderRepository.save(order);

        return order.getId();
    }
    //주문취소
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
        //jpa로 인해 자동으로 재고수량등이 업데이트된다.
    }
        //검색
    public List<Order> findOrders(OrderSearch orderSearch) {
            return orderRepository.findAllByString(orderSearch);
    }
}