package com.project.final_project.furniture.repository;

import com.project.final_project.furniture.domain.Furniture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FurnitureRepository extends JpaRepository<Furniture, Integer> {

  @Query("select f from Furniture f where f.mapId = :mapId and f.mapType = :mapType ")
  List<Furniture> getFurnitureListByMapIdAndMapType(@Param("mapId") Integer mapId, @Param("mapType") String mapType);

  @Query("select f from Furniture f where f.mapId = :userId")
  List<Furniture> getFurnitureListByUserId(@Param("userId") Integer userId);
}
