package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.constant.ItemSellStatus;
import com.catveloper365.studyshop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    /**
     * 테스트 데이터 생성
     *
     * @return Item
     */
    private Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        System.out.println(item);
        return item;
    }

    @Test
    @DisplayName("저장 테스트")
    public void save() {
        //given
        Item item = createItem();

        //when
        itemRepository.save(item);

        //then
        em.flush();
        em.clear(); //DB에서 조회해오도록 영속성 컨텍스트 비워주기

        Optional<Item> findItem = itemRepository.findById(item.getId());
        assertThat(item.getItemNm()).isEqualTo(findItem.orElse(null).getItemNm());
        assertThat(item.getStockNumber()).isEqualTo(findItem.orElse(null).getStockNumber());
    }

    @Test
    @DisplayName("삭제 테스트")
    void delete() {
        //given
        Item item = createItem();
        itemRepository.save(item);
        long saveCnt = itemRepository.count();
        assertThat(saveCnt).isEqualTo(1);

        //when
        itemRepository.delete(item);

        //then
        long removeCnt = itemRepository.count();
        assertThat(removeCnt).isEqualTo(0);
        Optional<Item> findItem = itemRepository.findById(item.getId());
        assertThat(findItem).isEmpty();
    }

    @Test
    @DisplayName("수정 테스트")
    void update() {
        //given
        Item item = createItem();
        Item savedItem = itemRepository.save(item);

        //when
        savedItem.setPrice(15000);
        savedItem.setItemSellStatus(ItemSellStatus.SOLD_OUT);
        em.flush();
        em.clear();

        //then
        Optional<Item> findItem = itemRepository.findById(savedItem.getId());
        assertThat(savedItem.getPrice()).isEqualTo(findItem.orElse(null).getPrice());
        assertThat(savedItem.getItemSellStatus()).isEqualTo(findItem.orElse(null).getItemSellStatus());
    }
}