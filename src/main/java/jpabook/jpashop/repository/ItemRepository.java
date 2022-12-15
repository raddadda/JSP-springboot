package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){

        if(item.getId()== null){
            //처음엔 저장된 id가 없기때문에, 즉 새로생성한 객체라면, 신규로 등록한다는 의미
            em.persist(item);
        } else {
            em.merge(item);
        }
    }
    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        //여러개 찾는것은 jpa를 사용해야한다.
        return em.createQuery("select i from Item i",Item.class)
                .getResultList();
    }
}
