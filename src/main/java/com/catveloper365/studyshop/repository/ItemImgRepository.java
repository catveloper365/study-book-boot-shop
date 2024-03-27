package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    /** 상품 이미지 아이디를 오름차순으로 가져옴 */
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    /** 상품의 대표 이미지 정보 조회 */
    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);
}
