package com.project.final_project.mapcontest.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.cloudinary.dto.CloudinaryResponseDTO;
import com.project.final_project.cloudinary.service.CloudinaryService;
import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.furniture.dto.FurnitureDTO;
import com.project.final_project.mapcontest.dto.MapContestDTO;
import com.project.final_project.mapcontest.dto.MapContestFurnitureVoDTO;
import com.project.final_project.mapcontest.dto.MapContestRegisterDTO;
import com.project.final_project.mapcontest.service.MapContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/map-contest")
@RequiredArgsConstructor
public class MapContestController {

  private final MapContestService mapContestService;
//  private final CloudinaryService cloudinaryService;


  //== cloudinary ==//
  // 이미지 업로드
//  @PostMapping(
//      value = "/upload-image",
//      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
//      produces = MediaType.APPLICATION_JSON_VALUE
//  )
//  public ResponseEntity<?> uploadCloudinaryImage(
//      @RequestPart("file")
//      @Parameter(description = "업로드할 파일 - cloudinary 버전") MultipartFile file) {
//    try {
//      Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);
//      return ResponseEntity.ok(
//          new CloudinaryResponseDTO(
//              (String)uploadResult.get("url"), (String)uploadResult.get("public_id")));
//    } catch (IOException e) {
//      return ResponseEntity.status(500).body(Map.of("error", "Image upload failed"));
//    }
//  }


  @GetMapping("/list")
  public ResponseResult<List<MapContestDTO>> getAllMapContestList() {
    return success(mapContestService.getAllMapContestList());
  }

  @GetMapping("/furniture-list/{mapContestId}")
  public List<MapContestFurnitureVoDTO> getFurnitureListByMapContestId(@PathVariable("mapContestId") Integer mapContestId) {
    return mapContestService.getFurnitureListByMapContestId(mapContestId);
  }

  @Operation(summary = "맵 콘테스트에 나만의 교실 스냅샷 등록", description = "맵 콘테스트에 나만의 교실 스냅샷을 등록합니다. 이때 furniture 데이터가 있어야 맵 콘테스트에 등록을 할 수가 있습니다.")
  @PostMapping
  public ResponseResult<MapContestDTO> registerMapContest(@RequestBody MapContestRegisterDTO dto){
    return success(mapContestService.registerMapContest(dto));
  }

  @PatchMapping
  public ResponseResult<MapContestDTO> updateMapContest(@RequestBody MapContestDTO dto){
    return success(mapContestService.updateMapContest(dto));
  }

  @DeleteMapping
  public ResponseResult<String> removeMapContest(@RequestParam("mapContestId") Integer mapContestId){
    mapContestService.removeMapContest(mapContestId);
    return success("remove success id : " + mapContestId);
  }

}
