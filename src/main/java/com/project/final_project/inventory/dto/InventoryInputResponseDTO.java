package com.project.final_project.inventory.dto;

import com.project.final_project.inventory.domain.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryInputResponseDTO {

  Integer inventoryId;
  Integer userId;
  Integer itemIdx;
  Integer count;
  String itemType;

  public InventoryInputResponseDTO(Inventory inventory, Integer itemIdx) {
    this.inventoryId = inventory.getId();
    this.userId = inventory.getUserId();
    this.itemIdx = itemIdx;
    this.count = inventory.getItemCountList().get(itemIdx);
    this.itemType = inventory.getItemType();
  }

}
