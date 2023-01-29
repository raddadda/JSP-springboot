package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;


@Getter
@Setter
public class MemberForm {
    @NotEmpty(message = "회원 이름은 필수 입니다")
    //null과 "" 둘다 허용하지 않는다. " " 은 된다.
    private String name;
    private String city;
    private String street;
    private String zipcode;

}
