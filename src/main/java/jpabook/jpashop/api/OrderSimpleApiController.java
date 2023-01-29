package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
       List<Order> all= orderRepository.findAllByString(new OrderSearch());
       for(Order order : all){
           order.getMember().getName(); //Lazy강제 초기화
           order.getDelivery().getAddress();//Lazy강제 초기화
        //order.getMamber까지는 프록시개체이다. 즉 db에 쿼리가 날라가지 않은것이다
           //여기에 getName을 하면 lazy를 강제로 초기화하는것이다.
       //웬만하면 dto로 변환해서 하기
       }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        //오더 2개
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        //2개
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }
    //fetch join을 이용한 지연로딩없는 성능최적화
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }
//v3와 다르게 필요한 부분만 select절에서 나오는것을 볼 수 있다.
    //화면상으로는 깔끔하지만 로직을 재활용하기는 어렵다
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderSimpleQueryRepository .findOrderDtos();
    }
    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderData;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId=order.getId();
            name=order.getMember().getName();//LAZY 초기화
            orderData=order.getOrderDate();
            orderStatus=order.getStatus();
            address = order.getDelivery().getAddress();//LAZY초기화
        }
    }

}
