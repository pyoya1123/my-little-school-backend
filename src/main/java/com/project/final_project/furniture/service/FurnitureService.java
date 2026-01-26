package com.project.final_project.furniture.service;

import com.project.final_project.furniture.domain.Furniture;
import com.project.final_project.furniture.dto.FurnitureCountDTO;
import com.project.final_project.furniture.dto.FurnitureDeleteDTO;
import com.project.final_project.furniture.dto.FurnitureRegisterDTO;
import com.project.final_project.furniture.dto.FurnitureDTO;
import com.project.final_project.furniture.dto.FurnitureUpdateDTO;
import com.project.final_project.furniture.repository.FurnitureRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class FurnitureService {

  private final FurnitureRepository furnitureRepository;

  public List<FurnitureDTO> getAllFurniture() {
    return furnitureRepository.findAll().stream()
        .map(f -> new FurnitureDTO(
            f.getId(),
            f.getObjId(),
            f.getX(),
            f.getY(),
            f.getRot(),
            f.getFlip(),
            f.getMapId(),
            f.getMapType()
            ))
        .toList();
  }

  public Integer registerFurniture(@RequestBody FurnitureRegisterDTO furnitureRegisterDTO) {
    Furniture newFurniture = Furniture.builder()
        .objId(furnitureRegisterDTO.getObjId())
        .x(furnitureRegisterDTO.getX())
        .y(furnitureRegisterDTO.getY())
        .rot(furnitureRegisterDTO.getRot())
        .flip(furnitureRegisterDTO.getFlip())
        .mapId(furnitureRegisterDTO.getMapId())
        .mapType(furnitureRegisterDTO.getMapType())
        .userId(furnitureRegisterDTO.getUserId())
        .build();
    Furniture savedFurniture = furnitureRepository.save(newFurniture);
    return savedFurniture.getId();
  }

  @Transactional
  public FurnitureUpdateDTO updateFurniture(FurnitureUpdateDTO furnitureUpdateDTO) {
    Integer id = furnitureUpdateDTO.getId();
    Furniture foundFurniture = furnitureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found furniture : " + id));

    if(furnitureUpdateDTO.getObjId() != null) {
      foundFurniture.setObjId(furnitureUpdateDTO.getObjId());
    }
    if(furnitureUpdateDTO.getX() != null) {
      foundFurniture.setX(furnitureUpdateDTO.getX());
    }
    if(furnitureUpdateDTO.getY() != null) {
      foundFurniture.setY(furnitureUpdateDTO.getY());
    }
    if(furnitureUpdateDTO.getRot() != null) {
      foundFurniture.setRot(furnitureUpdateDTO.getRot());
    }
    if(furnitureUpdateDTO.getFlip() != null){
      foundFurniture.setFlip(furnitureUpdateDTO.getFlip());
    }
    if(furnitureUpdateDTO.getMapId() != null) {
      foundFurniture.setMapId(furnitureUpdateDTO.getMapId());
    }
    if(furnitureUpdateDTO.getMapType() != null) {
      foundFurniture.setMapType(furnitureUpdateDTO.getMapType());
    }
    return furnitureUpdateDTO;
  }

  public void deleteFurniture(Integer furnitureId) {
    furnitureRepository.deleteById(furnitureId);
  }

  @Transactional
  public List<FurnitureDeleteDTO> deleteMyClassroomFurnitureListByUserIdForRemovedCount(Integer userId) {
    List<Furniture> furnitureList = furnitureRepository.getFurnitureListByUserId(userId);
    List<FurnitureDeleteDTO> res = new ArrayList<>();

    // objectId 기준으로 그룹화
    Map<Integer, List<Furniture>> groupedByObjectId = furnitureList.stream()
        .collect(Collectors.groupingBy(Furniture::getObjId));

    // 각 그룹을 삭제하고 삭제된 정보를 FurnitureDeleteDTO에 추가
    groupedByObjectId.forEach((objectId, items) -> {
      int removedCount = items.size();

      // FurnitureDeleteDTO에 삭제된 정보 추가
      res.add(new FurnitureDeleteDTO(objectId, removedCount));

      // 실제 삭제 수행
      furnitureRepository.deleteAll(items);
    });

    return res;
  }

  public boolean existsChatLogs() {
    return furnitureRepository.count() > 0;
  }

  public FurnitureDTO getFurnitureById(Integer id) {
    return new FurnitureDTO(furnitureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found furniture id : " + id)));
  }

  public List<FurnitureDTO> getFurnitureListByMapIdAndMapType(Integer mapId, String mapType) {
    List<Furniture> furnitureList = furnitureRepository.getFurnitureListByMapIdAndMapType(mapId, mapType);
    return furnitureList.stream().map(FurnitureDTO::new).toList();
  }

  public List<FurnitureCountDTO> getMyClassroomFurnitureListByUserIdForCount(Integer userId) {
    List<Furniture> furnitureList = furnitureRepository.getFurnitureListByUserId(userId);
    List<FurnitureCountDTO> res = new ArrayList<>();

    // objectId 기준으로 그룹화
    Map<Integer, List<Furniture>> groupedByObjectId = furnitureList.stream()
        .collect(Collectors.groupingBy(Furniture::getObjId));

    // 각 그룹을 삭제하고 삭제된 정보를 FurnitureDeleteDTO에 추가
    groupedByObjectId.forEach((objectId, items) -> {
      int count = items.size();

      // FurnitureDeleteDTO에 삭제된 정보 추가
      res.add(new FurnitureCountDTO(objectId, count));
    });

    return res;
  }

  public void deleteFurnitureListByUserId(Integer userId) {
    furnitureRepository.deleteAll(furnitureRepository.getFurnitureListByUserId(userId));
  }
}
