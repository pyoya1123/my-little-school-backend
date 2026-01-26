package com.project.final_project.item.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRegisterDTO {
  private Integer id;
  private Integer itemIdx;
  private String itemName;
  private Integer price;
  private String itemType;

}
