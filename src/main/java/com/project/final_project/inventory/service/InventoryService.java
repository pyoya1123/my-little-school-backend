package com.project.final_project.inventory.service;

import com.project.final_project.inventory.domain.Inventory;
import com.project.final_project.inventory.dto.InventoryDTO;
import com.project.final_project.inventory.dto.InventoryUpdateRequestDTO;
import com.project.final_project.inventory.repository.InventoryRepository;
import com.project.final_project.item.dto.ItemDTO;
import com.project.final_project.item.dto.UserItemDTO;
import com.project.final_project.item.service.ItemService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

  private final InventoryRepository inventoryRepository;
  private final ItemService itemService;

  @Transactional
  public void createInventory(Integer userId) {
    List<ItemDTO> allItem = itemService.getAllItem();

    Set<String> existingItemTypes = new HashSet<>(allItem.stream()
        .map(ItemDTO::getItemType)
        .toList());

    for (String existingItemType : existingItemTypes) {
      Inventory inventory = inventoryRepository.getInventoryByUserIdAndItemType(
          userId, existingItemType);

      if (inventory != null) {
        log.warn("Inventory already exists for userId={} and itemType={}", userId, existingItemType);
        throw new IllegalArgumentException("이미 인벤토리가 존재합니다.");
      }

      inventory = new Inventory();
      inventory.setUserId(userId);
      inventory.setItemType(existingItemType);

      // 초기화된 itemCountMap 추가 (50개의 0)
      Map<Integer, Integer> initialItemCountMap = new HashMap<>();
      for (int i = 0; i < 50; i++) {
        initialItemCountMap.put(i, 0);
      }

      inventory.setItemCountList(initialItemCountMap);

      // Inventory 저장
      inventoryRepository.save(inventory);
      log.info("New inventory created for userId={} and itemType={}", userId, existingItemType);
    }
  }

  @Transactional
  public InventoryDTO updateInventory(InventoryUpdateRequestDTO inventoryUpdateRequestDTO) {

    Inventory foundInventory = inventoryRepository.getInventoryByInventoryId(
        inventoryUpdateRequestDTO.getInventoryId());

    if (foundInventory == null) {
      log.error("Inventory not found for ID={}", inventoryUpdateRequestDTO.getInventoryId());
      throw new IllegalArgumentException("Inventory not found for ID: " + inventoryUpdateRequestDTO.getInventoryId());
    }

    Map<Integer, Integer> itemCountMap = foundInventory.getItemCountList();

    if (itemCountMap == null) {
      log.error("Item count map is not initialized for inventoryId={}", inventoryUpdateRequestDTO.getInventoryId());
      throw new IllegalStateException("Item count map is not initialized.");
    }

    int itemIdx = inventoryUpdateRequestDTO.getItemIdx();

    if (itemIdx < 0 || itemIdx >= 50) { // 25는 아이템 개수 제한
      log.error("Invalid item index={} for inventoryId={}", itemIdx, inventoryUpdateRequestDTO.getInventoryId());
      throw new IllegalArgumentException("Invalid item index: " + itemIdx);
    }

    // 특정 인덱스 업데이트
    itemCountMap.put(itemIdx, inventoryUpdateRequestDTO.getCount());
    log.info("Updated item index={} with count={} for inventoryId={}", itemIdx, inventoryUpdateRequestDTO.getCount(), inventoryUpdateRequestDTO.getInventoryId());

    // 변경된 맵을 엔티티에 반영
    foundInventory.setItemCountList(itemCountMap);

    return new InventoryDTO(foundInventory);
  }

  @Transactional
  public List<UserItemDTO> getItemListByUserIdAndItemType(Integer userId, String itemType) {
    List<UserItemDTO> res = new ArrayList<>();

    try {
      Inventory foundInventory = inventoryRepository.getInventoryByUserIdAndItemType(userId, itemType);

      // Inventory가 없으면 생성
      if (foundInventory == null) {
        log.warn("Inventory not found for userId={} and itemType={}. Creating a new inventory.", userId, itemType);

        foundInventory = new Inventory();
        foundInventory.setUserId(userId);
        foundInventory.setItemType(itemType);

        // itemCountMap 초기화 (25개의 0)
        Map<Integer, Integer> itemCountMap = new HashMap<>();
        for (int i = 0; i < 50; i++) {
          itemCountMap.put(i, 0);
        }
        foundInventory.setItemCountList(itemCountMap);

        // 새로운 Inventory 저장
        inventoryRepository.save(foundInventory);
        log.info("New inventory created for userId={} and itemType={}", userId, itemType);
      }

      Map<Integer, Integer> itemCountMap = foundInventory.getItemCountList();
      List<ItemDTO> itemList = itemService.getItemListByItemType(itemType);

      for (int i = 0; i < itemList.size(); i++) {
        ItemDTO itemDTO = itemList.get(i);
        int itemCount = itemCountMap.getOrDefault(i, 0); // 키가 없으면 기본값 0
        res.add(new UserItemDTO(itemDTO, foundInventory.getId(), itemCount));
      }

    } catch (IllegalArgumentException e) {
      log.error("Illegal argument error: {}", e.getMessage(), e);
      throw e; // 필요하면 사용자 정의 예외로 감쌀 수 있음
    } catch (IllegalStateException e) {
      log.error("Illegal state error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error occurred while retrieving inventory for userId={} and itemType={}: {}", userId, itemType, e.getMessage(), e);
      throw new RuntimeException("An unexpected error occurred. Please try again later.", e);
    }

    return res;
  }

  @Transactional
  public void deleteInventoryByUserId(Integer userId) {
      inventoryRepository.deleteAll(inventoryRepository.getInventoryListByUserId(userId));
  }
}
