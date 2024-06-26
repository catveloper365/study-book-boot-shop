package com.catveloper365.studyshop.service;

import com.catveloper365.studyshop.constant.ItemSellStatus;
import com.catveloper365.studyshop.dto.ItemFormDto;
import com.catveloper365.studyshop.entity.Item;
import com.catveloper365.studyshop.entity.ItemImg;
import com.catveloper365.studyshop.repository.ItemImgRepository;
import com.catveloper365.studyshop.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles() throws Exception{
        List<MultipartFile> multipartFileList = new ArrayList<>();

        //가짜 Mock MultipartFile 만들기
        for (int i = 0; i < 5; i++) {
            String path = "D:/project/shop/item/";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile = new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4});
            multipartFileList.add(multipartFile);
        }

        return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 테스트")
//    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem() throws Exception {
        //given
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm("테스트상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("테스트 상품 입니다.");
        itemFormDto.setPrice(1000);
        itemFormDto.setStockNumber(100);

        List<MultipartFile> multipartFileList = createMultipartFiles();

        //when
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList);

        //then
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        Item findItem = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        assertEquals(itemFormDto.getItemNm(), findItem.getItemNm());
        assertEquals(itemFormDto.getItemSellStatus(), findItem.getItemSellStatus());
        assertEquals(itemFormDto.getItemDetail(), findItem.getItemDetail());
        assertEquals(itemFormDto.getPrice(), findItem.getPrice());
        assertEquals(itemFormDto.getStockNumber(), findItem.getStockNumber());
        assertEquals(multipartFileList.get(0).getOriginalFilename(), itemImgList.get(0).getOriImgName());

    }

}