package jpabook.jpashop.domain.item;


import jpabook.jpashop.domain.Category;
import jpabook.jpashop.controller.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
//부모 클래스에 선언하며, 하위 클래스를 구분하는 용도의 컬럼이다.
@Getter @Setter
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //비즈니스 로직//

    //stock(재고) 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    //재고 감소
    public void removeStock(int quantity){
        int restStock=this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}

