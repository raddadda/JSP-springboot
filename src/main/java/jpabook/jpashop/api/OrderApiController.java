package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@Controller는 주로 view를 반호나하기 위해 사용하는것이다.
//그에 반해 @RestController는 json 형태로 객체 데이터를 반환하는 것이다.
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();//아이템 강제 초기화
                //아이템안의 개별 아이템 초기화
            //forEach..?
            orderItems.stream().forEach(o -> o.getItem().getName());
            }

        return all;
        }
        // 이렇게 초기화 시키는 이유는 하이버네이트5가 기본설정이 프록시 인것은 데이터를 뿌리지 않기 때문이다.
        //그런데 위에처럼 프록시를 강제 초기화해서 보여줄 수 있도록 하는 것이다.
        //이때 양방향의존관계일경우 한쪽에 jsonignore 어노테이션을 해줘야한다.

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    //페이징을 할 수 없는 단점이 있다.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();

     /*   for(Order order : orders) {
            System.out.println("order ref=" + order + " id=" + order.getId());
            //포스트맨에서 실행해보면 두 같은 order의 참조값까지 같은것을 알 수 있다.
        }*/

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue ="0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100")int limit){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);

     /*   for(Order order : orders) {
            System.out.println("order ref=" + order + " id=" + order.getId());
            //포스트맨에서 실행해보면 두 같은 order의 참조값까지 같은것을 알 수 있다.
        }*/

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;

        //결과는 1*n*m로 엄청난 쿼리가 발생한다.
        //v3의 장점은 쿼리가 한방에 나간다는점이지만 대신에 DB로 모든 데이터가 전송이 되어 용량이 많아지는 단점이 있다.
        //v3.1은 쿼리가 여러개 나간다는 단점이 있지만 중복없이 필요한 정보만 가져오기 때문에 위보다 용량이 적다.
    }
    @Getter
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;
        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            //order.getOrderItems().stream().forEach(o->o.getItem().getName());
            //이렇게 단순히 Dto로 한번 감싸면 오더아이템에서 수정을하면 전체가 바뀐다.
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto{

        private String itemName; //상품명
        private int orderPrice; //주문 가격
        private int count; //주문 수량
        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
