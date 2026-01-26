package com.project.final_project.inventory.repository;

import com.project.final_project.inventory.domain.Inventory;
import com.project.final_project.item.domain.Item;
import com.project.final_project.item.dto.ItemDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {


  @Query("select i from Inventory i where i.id = :inventoryId")
  Inventory getInventoryByInventoryId(@Param("inventoryId") Integer inventoryId);

  @Query("select i from Inventory i where i.userId = :userId and i.itemType = :itemType")
  Inventory getInventoryByUserIdAndItemType(
      @Param("userId") Integer userId,
      @Param("itemType") String itemType);

  @Query("select i from Inventory i where i.userId = :userId")
  List<Inventory> getInventoryListByUserId(@Param("userId") Integer userId);
}
