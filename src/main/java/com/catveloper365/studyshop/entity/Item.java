package com.catveloper365.studyshop.entity;

import com.catveloper365.studyshop.constant.ItemSellStatus;
import com.catveloper365.studyshop.dto.ItemFormDto;
import com.catveloper365.studyshop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.thymeleaf.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name="price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    /** 상품 정보 수정 */
    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    /** 주문 수량 만큼 재고 감소 */
    public void removeStock(int orderCount) {
        int restStock = this.stockNumber - orderCount; //주문 후 남은 재고 수량
        if (restStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량: " + this.stockNumber + ")");
        } else if (restStock == 0){ //재고가 모두 소진되면 품절 상태로 변경
            this.itemSellStatus = ItemSellStatus.SOLD_OUT;
        }
        this.stockNumber = restStock;
    }

    /** 주문 수량 만큼 재고 증가 */
    public void addStock(int orderCount) {
        this.stockNumber += orderCount;

        //품절이었던 상품의 재고가 증가하면 상품 판매 상태를 판매중으로 변경
        if (StringUtils.equals(this.itemSellStatus, ItemSellStatus.SOLD_OUT)
                && this.stockNumber > 0) {
            this.itemSellStatus = ItemSellStatus.SELL;
        }
    }
}
