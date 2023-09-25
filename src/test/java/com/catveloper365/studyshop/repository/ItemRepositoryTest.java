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
import java.util.List;
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
     */
    private Item createItem() {
        Item item = createItem("테스트 상품", 10000, "테스트 상품 상세 설명");
        return item;
    }

    private Item createItem(String itemNm, int price, String itemDetail) {
        Item item = new Item();
        item.setItemNm(itemNm);
        item.setPrice(price);
        item.setItemDetail(itemDetail);
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
//        System.out.println(item);
        return item;
    }

    private void createItemList() {
        for (int i = 1; i <= 10; i++) {
            Item item = createItem(("테스트 상품" + i), (10000 + i), ("테스트 상품 상세 설명" + i));
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("저장")
    public void save() {
        //given
        Item item = createItem();

        //when
        itemRepository.save(item);

        //then
        em.flush();
        em.clear(); //DB에서 조회해오도록 영속성 컨텍스트 비워주기

        Optional<Item> findItem = itemRepository.findById(item.getId());
        assertThat(item.getItemNm()).isEqualTo(findItem.orElseGet(Item::new).getItemNm());
        assertThat(item.getStockNumber()).isEqualTo(findItem.orElseGet(Item::new).getStockNumber());
    }

    @Test
    @DisplayName("삭제")
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
        assertThat(findItem.orElseGet(Item::new).getItemNm()).isEqualTo(null);
        assertThat(findItem.orElseGet(Item::new).getPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("수정")
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
        assertThat(savedItem.getPrice()).isEqualTo(findItem.orElseGet(Item::new).getPrice());
        assertThat(savedItem.getItemSellStatus()).isEqualTo(findItem.orElseGet(Item::new).getItemSellStatus());
    }

    @Test
    @DisplayName("상품명 조회")
    void findByItemNmTest() {
        //given
        this.createItemList();

        //when
        String findItemNm = "테스트 상품5";
        List<Item> itemList = itemRepository.findByItemNm(findItemNm);

        //then
//        for (Item item : itemList) {
//            System.out.println("상품명 조회 결과 = " + item);
//        }
        assertThat(itemList.size()).isEqualTo(1);
        assertThat(findItemNm).isEqualTo(itemList.get(0).getItemNm());
    }

    @Test
    @DisplayName("상품명, 상품 상세 설명 모두 존재")
    public void findNmOrDetailBoth() throws Exception {
        //given
        this.createItemList();

        //when
        String findItemNm = "테스트 상품10"; //DB에 있는 데이터
        String findItemDetail = "테스트 상품 상세 설명5"; //DB에 있는 데이터
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail(findItemNm, findItemDetail);

        //then
//        for (Item item : itemList) {
//            System.out.println("item = " + item);
//        }
        assertThat(itemList.size()).isEqualTo(2);
        assertThat(findItemNm).isIn(itemList.get(0).getItemNm(), itemList.get(1).getItemNm());
    }

    @Test
    @DisplayName("상품명, 상품 상세 설명 중 1개만 존재")
    void findNmOrDetailOne() throws Exception {
        //given
        this.createItemList();

        //when
        String findItemNm = "테스트 상품"; //DB에 없는 데이터
        String findItemDetail = "테스트 상품 상세 설명1"; //DB에 있는 데이터
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail(findItemNm, findItemDetail);

        //then
//        for (Item item : itemList) {
//            System.out.println("item = " + item);
//        }
        assertThat(itemList.size()).isEqualTo(1);
        assertThat(findItemDetail).isEqualTo(itemList.get(0).getItemDetail());
    }


}