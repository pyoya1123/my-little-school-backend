package com.project.final_project.item.dto;

import com.project.final_project.item.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
  private Integer id;
  private Integer itemIdx;
  private String itemName;
  private Integer price;
  private String itemType;

  public ItemDTO(Item item) {
    this.id = item.getId();
    this.itemIdx = item.getItemIdx();
    this.itemName = item.getItemName();
    this.price = item.getPrice();
    this.itemType = item.getItemType();
  }

}
