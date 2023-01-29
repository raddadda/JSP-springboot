package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;


    @NotEmpty
    //api에서 이름을 입력하지 않았을때도 null로 들어가는것을 막기위함.
    private String name;


    @Embedded
    private Address address;

    @JsonIgnore
    //양방향연관관계가 있으면 한쪽에 해줘야한다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders;

    public Member(){
        orders = new ArrayList<>();
    }
}
