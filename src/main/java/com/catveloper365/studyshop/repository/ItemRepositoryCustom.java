package com.catveloper365.studyshop.repository;

import com.catveloper365.studyshop.dto.ItemSearchDto;
import com.catveloper365.studyshop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
