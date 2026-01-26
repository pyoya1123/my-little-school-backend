package com.project.final_project.inventory.dto;

import com.project.final_project.inventory.domain.Inventory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InventoryDTO {
  private Integer inventoryId;
  private Integer userId;
  private List<Integer> itemCountList;
  private String itemType;

  public InventoryDTO(Inventory inventory) {
    this.inventoryId = inventory.getId();
    this.userId = inventory.getUserId();
    this.itemType = inventory.getItemType();

    // Map<Integer, Integer> -> List<Integer> 변환
    Map<Integer, Integer> itemCountMap = inventory.getItemCountList();

    // 키를 기준으로 정렬한 후 값만 리스트에 추가
    this.itemCountList = itemCountMap.entrySet().stream()
        .sorted(Map.Entry.comparingByKey()) // 키를 기준으로 정렬
        .map(Map.Entry::getValue) // 값만 추출
        .collect(Collectors.toList()); // 리스트로 변환
  }
}
