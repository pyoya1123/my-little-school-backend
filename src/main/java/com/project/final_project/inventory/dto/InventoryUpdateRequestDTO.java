package com.project.final_project.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdateRequestDTO {
  private Integer inventoryId;
  private Integer itemIdx;
  private Integer count;
  private String itemType;
}
