package com.catveloper365.studyshop.controller;

import com.catveloper365.studyshop.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/thymeleaf")
public class ThymeleafExController {
    public static final String EX_TEMPLATES_PATH_NAME = "thymeleafEx/thymeleafEx";

    @GetMapping(value = "/ex01")
    public String thymeleafExample01(Model model) {
        model.addAttribute("data", "타임리프 예제 입니다.");
        return EX_TEMPLATES_PATH_NAME + "01";
    }

    @GetMapping("/ex02")
    public String thymeleafExample02(Model model) {
        ItemDto itemDto = getItemDtoList(1).get(0);
        model.addAttribute("itemDto", itemDto);
        return EX_TEMPLATES_PATH_NAME + "02";
    }

    @GetMapping("/ex03")
    public String thymeleafExample03(Model model) {
        List<ItemDto> itemDtoList = getItemDtoList(10);
        model.addAttribute("itemDtoList", itemDtoList);
        return EX_TEMPLATES_PATH_NAME + "03";
    }

    @GetMapping("/ex04")
    public String thymeleafExample04(Model model) {
        List<ItemDto> itemDtoList = getItemDtoList(10);
        model.addAttribute("itemDtoList", itemDtoList);
        return EX_TEMPLATES_PATH_NAME + "04";
    }

    @GetMapping("/ex05")
    public String thymeleafExample05() {
        return EX_TEMPLATES_PATH_NAME + "05";
    }

    @GetMapping("/ex06")
    public String thymeleafExample06(String param1, String param2, Model model) {
        model.addAttribute("param1", param1);
        model.addAttribute("param2", param2);
        return EX_TEMPLATES_PATH_NAME + "06";
    }

    @GetMapping("/ex07")
    public String thymeleafExample07() {
        return EX_TEMPLATES_PATH_NAME + "07";
    }

    private static List<ItemDto> getItemDtoList(int totalCount) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (int i = 1; i <= totalCount; i++) {
            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명" + i);
            itemDto.setItemNm("테스트 상품" + i);
            itemDto.setPrice(1000 * i);
            itemDto.setRegTime(LocalDateTime.now());

            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }
}
