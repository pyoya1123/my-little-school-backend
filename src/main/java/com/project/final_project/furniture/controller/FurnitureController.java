package com.project.final_project.furniture.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.furniture.domain.Furniture;
import com.project.final_project.furniture.dto.FurnitureCountDTO;
import com.project.final_project.furniture.dto.FurnitureDeleteDTO;
import com.project.final_project.furniture.dto.FurnitureRegisterDTO;
import com.project.final_project.furniture.dto.FurnitureDTO;
import com.project.final_project.furniture.dto.FurnitureUpdateDTO;
import com.project.final_project.furniture.repository.FurnitureRepository;
import com.project.final_project.furniture.service.FurnitureService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/furniture")
@RequiredArgsConstructor
public class FurnitureController {

  private final FurnitureService furnitureService;

  @GetMapping("/list")
  public List<FurnitureDTO> getAllFurniture() {
    return furnitureService.getAllFurniture();
  }

  @GetMapping("/list/map/{mapId}/{mapType}")
  public List<FurnitureDTO> getFurnitureListByMapIdAndMapType(
      @PathVariable("mapId") Integer mapId, @PathVariable("mapType") String mapType) {
    return furnitureService.getFurnitureListByMapIdAndMapType(mapId, mapType);
  }

  @GetMapping
  public FurnitureDTO getFurnitureById(@RequestParam("furnitureId") Integer id) {
    return furnitureService.getFurnitureById(id);
  }

  @GetMapping("/list/{userId}")
  public ResponseResult<List<FurnitureCountDTO>> getMyClassroomFurnitureListByUserId(@PathVariable("userId") Integer userId) {
    return success(furnitureService.getMyClassroomFurnitureListByUserIdForCount(userId));
  }

  @PostMapping
  public ResponseEntity<Integer> registerFurniture(@RequestBody FurnitureRegisterDTO furnitureRegisterDTO) {
    return ResponseEntity.ok(furnitureService.registerFurniture(furnitureRegisterDTO));
   }

  @PatchMapping
  public ResponseEntity<FurnitureUpdateDTO> updateFurniture(@RequestBody FurnitureUpdateDTO furnitureUpdateDTO) {
    return ResponseEntity.ok(furnitureService.updateFurniture(furnitureUpdateDTO));
  }

  @DeleteMapping
  public ResponseEntity<?> removeFurniture(@RequestParam("furnitureId") Integer furnitureId) {
    furnitureService.deleteFurniture(furnitureId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/list/{userId}")
  public ResponseEntity<?> deleteMyClassroomFurnituresByUserId(@PathVariable("userId") Integer userId) {
    furnitureService.deleteFurnitureListByUserId(userId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/list/{userId}")
  public ResponseResult<List<FurnitureDeleteDTO>> deleteMyClassroomFurnitureListByUserId(@PathVariable("userId") Integer userId) {
    return success(furnitureService.deleteMyClassroomFurnitureListByUserIdForRemovedCount(userId));
  }
}
