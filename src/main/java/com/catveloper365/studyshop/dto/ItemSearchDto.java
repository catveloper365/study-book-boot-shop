package com.catveloper365.studyshop.dto;

import com.catveloper365.studyshop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {
    private ItemSellStatus searchSellStatus; //상품 판매 상태

    //지정한 조건 이후로 등록된 상품 조회(all, 1d, 1w, 1m, 6m)
    private String searchDateType; //상품 등록 기간

    private String searchBy; //상품 조회 유형(itemNm, createdBy)

    private String searchQuery = ""; //조회 유형에 대한 검색어
}
