package com.project.final_project.item.service;

import com.project.final_project.item.domain.Item;
import com.project.final_project.item.dto.ItemDTO;
import com.project.final_project.item.dto.ItemRegisterDTO;
import com.project.final_project.item.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

  private final ItemRepository itemRepository;

  public ItemDTO registerItem(ItemRegisterDTO dto) {
    Item item = new Item(dto);
    itemRepository.save(item);
    return new ItemDTO(item);
  }

  @Transactional
  public ItemDTO updateInventory(ItemDTO dto) {

    Item foundItem = itemRepository.findById(dto.getId()).orElseThrow(
        () -> new IllegalStateException("not found item id : " + dto.getId()));

    if(dto.getItemIdx() != null){
      foundItem.setItemIdx(dto.getItemIdx());
    }

    if(dto.getItemName() != null){
      foundItem.setItemName(dto.getItemName());
    }

    if(dto.getPrice() != null){
      foundItem.setPrice(dto.getPrice());
    }

    if(dto.getItemType() != null){
      foundItem.setItemType(dto.getItemType());
    }

    return new ItemDTO(foundItem);
  }

  public void deleteItemById(Integer itemId) {
    itemRepository.deleteById(itemId);
  }

  public ItemDTO getItemByItemId(Integer itemId) {
    return new ItemDTO(itemRepository.findById(itemId).orElseThrow(
        () -> new IllegalStateException("not found item id : " + itemId)));
  }

  public List<ItemDTO> getAllItem() {
    return itemRepository.findAll().stream().map(ItemDTO::new).toList();
  }

  public List<ItemDTO> getItemListByItemType(String itemType) {
    return itemRepository.getItemListByItemType(itemType).stream().map(ItemDTO::new).toList();
  }

  public Boolean existItemDatas() {
    return !itemRepository.findAll().isEmpty();
  }
}
