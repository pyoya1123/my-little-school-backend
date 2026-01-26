package com.project.final_project.mapcontest.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.final_project.cloudinary.service.CloudinaryService;
import com.project.final_project.furniture.repository.FurnitureRepository;
import com.project.final_project.furniture.service.FurnitureService;
import com.project.final_project.gallery.domain.Gallery;
import com.project.final_project.mapcontest.domain.MapContest;
import com.project.final_project.mapcontest.dto.MapContestDTO;
import com.project.final_project.mapcontest.dto.MapContestFurnitureVoDTO;
import com.project.final_project.mapcontest.dto.MapContestRegisterDTO;
import com.project.final_project.mapcontest.repository.MapContestRepository;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MapContestService {

  private final MapContestRepository mapContestRepository;
  private final FurnitureService furnitureService;
//  private final CloudinaryService cloudinaryService;

  public MapContestDTO registerMapContest(MapContestRegisterDTO dto) {

    // 깊은 복사 수행
    List<MapContestFurnitureVoDTO> copiedFurnitureList = furnitureService
        .getFurnitureListByMapIdAndMapType(dto.getUserId(), "MyClassroom").stream()
        .map(f -> new MapContestFurnitureVoDTO(
            f.getObjId(),
            f.getX(),
            f.getY(),
            f.getRot(),
            f.getFlip(),
            f.getMapId(),
            f.getMapType())
        )
        .collect(Collectors.toList());

    MapContest mapContest = new MapContest(
        dto.getUserId(),
        dto.getTitle(),
        dto.getDescription(),
        copiedFurnitureList,
        dto.getPreviewImageUrl(),
        0,
        0,
        dto.getPublicId()
    );

    MapContest savedMapContest = mapContestRepository.save(mapContest);

    return new MapContestDTO(savedMapContest);
  }

  @Transactional
  public void removeMapContest(Integer mapContestId) {
    MapContest foundMapContest = mapContestRepository.findById(mapContestId).orElseThrow(
        () -> new IllegalStateException("not found mapContest id : " + mapContestId));

    // furnitureList 초기화 및 삭제
    foundMapContest.getFurnitureList().clear();

//    cloudinaryDelete(foundMapContest.getPublicId());

    mapContestRepository.deleteById(mapContestId);
  }

  public List<MapContestDTO> getAllMapContestList() {
    return mapContestRepository
        .findAll()
        .stream()
        .sorted((m1, m2) -> Integer.compare(m2.getLikeCount(), m1.getLikeCount())) // likeCount 기준 내림차순 정렬
        .map(MapContestDTO::new)
        .toList();
  }

  public List<MapContestFurnitureVoDTO> getFurnitureListByMapContestId(Integer mapContestId) {
    MapContest mapContest = mapContestRepository.findById(mapContestId).orElseThrow(
        () -> new IllegalStateException("not found mapcontest id : " + mapContestId));

    return mapContest.getFurnitureList()
        .stream()
        .map(f -> new MapContestFurnitureVoDTO(
        f.getObjId(),
        f.getX(),
        f.getY(),
        f.getRot(),
        f.getFlip(),
        f.getUserId(),
        f.getMapType())
        ).toList();
  }

  @Transactional
  public MapContestDTO updateMapContest(MapContestDTO dto) {

    // 기존의 MapContest 엔티티 조회
    MapContest foundMapContest = mapContestRepository.findById(dto.getId()).orElseThrow(
        () -> new IllegalStateException("not found mapContest id : " + dto.getId())
    );

    // DTO의 값으로 엔티티 업데이트
    if (dto.getTitle() != null) {
      foundMapContest.setTitle(dto.getTitle());
    }

    if (dto.getDescription() != null) {
      foundMapContest.setDescription(dto.getDescription());
    }

    if (dto.getPreviewImageUrl() != null) {
      foundMapContest.setPreviewImageUrl(dto.getPreviewImageUrl());
    }

    if (dto.getLikeCount() != null) {
      foundMapContest.setLikeCount(dto.getLikeCount());
    }

    if (dto.getViewCount() != null) {
      foundMapContest.setViewCount(dto.getViewCount());
    }

    if (dto.getUserId() != null) {
      foundMapContest.setUserId(dto.getUserId());
    }

    // furnitureList 업데이트
    if (dto.getFurnitureList() != null) {
      List<MapContestFurnitureVoDTO> updatedFurnitureList = dto.getFurnitureList();
      foundMapContest.setFurnitureList(updatedFurnitureList);
    }

    if(dto.getPublicId() != null){
      foundMapContest.setPublicId(dto.getPublicId());
    }

    // 변경된 엔티티를 저장
    MapContest updatedMapContest = mapContestRepository.save(foundMapContest);

    // 업데이트된 엔티티를 기반으로 새 DTO 반환
    return new MapContestDTO(updatedMapContest);
  }

//  @Transactional
//  public void deleteMapContestListByUserId(Integer userId) {
//    List<MapContest> foundMapContestList = mapContestRepository.getMapContestListByUserId(userId);
//    // furnitureList 초기화 및 삭제
//    foundMapContestList.forEach(m -> {
//      m.getFurnitureList().clear();
//      cloudinaryDelete(m.getPublicId());
//    });
//    mapContestRepository.deleteAll(foundMapContestList);
//  }

  public List<MapContestDTO> getMapContestListByUserId(Integer userId) {
    return mapContestRepository.getMapContestListByUserId(userId)
        .stream().map(MapContestDTO::new).toList();
  }

//  public void cloudinaryDelete(String publicId) {
//    try {
//      cloudinaryService.deleteImage(publicId);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
}
