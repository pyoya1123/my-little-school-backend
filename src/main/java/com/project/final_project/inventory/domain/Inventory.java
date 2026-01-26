package com.project.final_project.inventory.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "item_type")
  private String itemType;

  @ElementCollection
  @CollectionTable(name = "user_item_count_list", joinColumns = @JoinColumn(name = "inventory_id"))
  @MapKeyColumn(name = "item_index")
  @Column(name = "item_count_list")
  private Map<Integer, Integer> itemCountList = new HashMap<>();

}
