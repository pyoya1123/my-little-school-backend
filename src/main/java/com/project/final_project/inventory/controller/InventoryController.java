package com.project.final_project.inventory.controller;

import com.project.final_project.inventory.dto.InventoryDTO;
import com.project.final_project.inventory.dto.InventoryUpdateRequestDTO;
import com.project.final_project.inventory.service.InventoryService;
import com.project.final_project.item.dto.UserItemDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

  private final InventoryService inventoryService;

  @PatchMapping
  public InventoryDTO updateInventory(@RequestBody InventoryUpdateRequestDTO inventoryUpdateRequestDTO) {
    return inventoryService.updateInventory(inventoryUpdateRequestDTO);
  }

  @GetMapping("/list/{userId}/{itemType}")
  public List<UserItemDTO> getItemListByUserIdAndItemType(
      @PathVariable("userId") Integer userId,
      @PathVariable("itemType") String itemType) {

    return inventoryService.getItemListByUserIdAndItemType(userId, itemType);
  }

}
