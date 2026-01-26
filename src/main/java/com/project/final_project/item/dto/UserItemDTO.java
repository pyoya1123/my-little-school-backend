package com.project.final_project.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserItemDTO {

  private Integer inventoryId;
  private Integer itemIdx;
  private String itemName;
  private Integer price;
  private String itemType;
  private Integer count;

  public UserItemDTO(ItemDTO dto, Integer inventoryId, Integer count) {
    this.inventoryId = inventoryId;
    this.itemIdx = dto.getItemIdx();
    this.itemName = dto.getItemName();
    this.price = dto.getPrice();
    this.itemType = dto.getItemType();
    this.count = count;
  }

}
