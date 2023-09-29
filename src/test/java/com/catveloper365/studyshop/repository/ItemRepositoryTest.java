package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.constant.ItemSellStatus;
import com.catveloper365.studyshop.entity.Item;
import com.catveloper365.studyshop.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

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
        Item item = createItem("테스트 상품", 10000, "테스트 상품 상세 설명", ItemSellStatus.SELL);
        return item;
    }

    private Item createItem(String itemNm, int price, String itemDetail, ItemSellStatus itemSellStatus) {
        Item item = new Item();
        item.setItemNm(itemNm);
        item.setPrice(price);
        item.setItemDetail(itemDetail);
        item.setItemSellStatus(itemSellStatus);
        item.setStockNumber(100);
        if(itemSellStatus.equals(ItemSellStatus.SOLD_OUT)){
            item.setStockNumber(0);
        }
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
//        System.out.println(item);
        return item;
    }

    private void createItemList() {
        for (int i = 1; i <= 10; i++) {
            Item item = createItem(("테스트 상품" + i), (10000 + i), ("테스트 상품 상세 설명" + i), ItemSellStatus.SELL);
            itemRepository.save(item);
        }
    }

    private void createItemList2() {
        for (int i = 1; i <= 5; i++) {
            Item item = createItem("테스트 상품" + i, 10000 + i, "테스트 상품 상세 설명" + i, ItemSellStatus.SELL);
            itemRepository.save(item);
        }
        for (int i = 6; i <= 10; i++) {
            Item item = createItem("테스트 상품" + i, 10000 + i, "테스트 상품 상세 설명" + i, ItemSellStatus.SOLD_OUT);
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
    void findByItemNm() {
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
    public void findByNmOrDetailBoth() throws Exception {
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
    void findByNmOrDetailOne() throws Exception {
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

    @Test
    @DisplayName("가격 LessThan 상품 오름차순 조회")
    void findByPriceLessThanAsc() throws Exception {
        //given
        this.createItemList();

        //when
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);

        //then
        for (Item item : itemList) {
            System.out.println("item = " + item);
        }
        assertThat(itemList.size()).isEqualTo(4);
        assertThat(itemList.get(0).getPrice()).isBetween(10001, 10004);
        assertThat(itemList.get(0).getPrice()).isEqualTo(10001);
    }
    
    @Test
    @DisplayName("가격 LessThan 상품 내림차순 조회")
    void findByPriceLessThanDesc() throws Exception {
        //given
        this.createItemList();

        //when
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);

        //then
        for (Item item : itemList) {
            System.out.println("item = " + item);
        }
        assertThat(itemList.size()).isEqualTo(4);
        assertThat(itemList.get(0).getPrice()).isEqualTo(10004);
    }

    @Test
    @DisplayName("@Query, JPQL, @Param 사용 상품 조회")
    void findByItemDetailByParam() throws Exception {
        //given
        this.createItemList();

        //when
        List<Item> itemList = itemRepository.findByItemDetailByParam("테스트");

        //then
        for (Item item : itemList) {
            System.out.println("item = " + item);
        }
        assertThat(itemList.size()).isEqualTo(10);
        assertThat(itemList.get(0).getPrice()).isEqualTo(10010);
    }

    @Test
    @DisplayName("@Query, JPQL, ?파라미터 번호 사용 상품 조회")
    void findByItemDetailByOrder() throws Exception {
        //given
        this.createItemList();

        //when
        List<Item> itemList = itemRepository.findByItemDetailByOrder("테스트");

        //then
        for (Item item : itemList) {
            System.out.println("item = " + item);
        }
        assertThat(itemList.size()).isEqualTo(10);
        assertThat(itemList.get(0).getPrice()).isEqualTo(10010);
    }

    @Test
    @DisplayName("@Query, nativeQuery, @Param 사용 상품 조회")
    void findByItemDetailByNative() throws Exception {
        //given
        this.createItemList();

        //when
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트");

        //then
        for (Item item : itemList) {
            System.out.println("item = " + item);
        }
        assertThat(itemList.size()).isEqualTo(10);
        assertThat(itemList.get(0).getPrice()).isEqualTo(10010);
    }

    @Test
    @DisplayName("Querydsl, JPAQueryFactory 사용 상품 조회")
    void querydslByJPAQueryFactory() throws Exception {
        //given
        this.createItemList();

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;

        //when
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트" + "%"))
                .orderBy(qItem.price.desc());

//        List<Item> itemList = query.fetch(); //조회 결과 전체
        Item findItem = query.fetchFirst(); //조회 결과 중 1건만

        //then
//        for (Item item : itemList) {
//            System.out.println("item = " + item);
//        }
//        assertThat(itemList.size()).isEqualTo(10);
//        assertThat(itemList.get(0).getPrice()).isEqualTo(10010);

        System.out.println("findItem = " + findItem);
        assertThat(findItem.getPrice()).isEqualTo(10010);
    }

    @Test
    @DisplayName("Querydsl, QuerydslPredicateExecutor 사용 상품 조회")
    void querydslByPredicateExecutor() throws Exception {
        //given
        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;

        //검색 조건
        String itemDetail = "테스트";
        int price = 10003;
        String itemSellStatus = "SELL";

        //when
        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price)); //gt는 보다 큰 값만 검색(> 조건)

        //조회 조건 동적 할당(판매 상태 검색 조건이 SELL인 경우에만 조건 사용)
        if (StringUtils.equals(itemSellStatus, ItemSellStatus.SELL)) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        //페이징하여 조회(조회할 페이지 번호, 한 페이지당 조회할 데이터 갯수)
        Pageable pageable = PageRequest.of(0, 5);
        //QuerydslPredicateExecutor 인터페이스에 정의된 findAll 사용
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);

        //then
        System.out.println("itemPagingResult.getTotalElements() = " + itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for (Item resultItem : resultItemList) {
            System.out.println("resultItem = " + resultItem);
        }

        assertThat(itemPagingResult.getTotalElements()).isEqualTo(2);
        assertThat(resultItemList.get(0).getPrice()).isIn(10004, 10005);
    }
}