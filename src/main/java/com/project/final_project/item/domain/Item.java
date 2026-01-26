package com.project.final_project.item.domain;

import com.project.final_project.item.dto.ItemRegisterDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

  @Id
  private Integer id;

  @Column(name = "item_idx")
  private Integer itemIdx;

  @Column(name = "item_name")
  private String itemName;

  @Column(name = "item_price")
  private Integer price;

  @Column(name = "item_type")
  private String itemType;

  public Item(ItemRegisterDTO dto) {
    this.id = dto.getId();
    this.itemIdx = dto.getItemIdx();
    this.itemName = dto.getItemName();
    this.price = dto.getPrice();
    this.itemType = dto.getItemType();
  }

}
