package com.project.final_project.gallery.controller;

import static com.project.final_project.common.global.HttpResponseEntity.success;

import com.project.final_project.cloudinary.dto.CloudinaryResponseDTO;
import com.project.final_project.cloudinary.service.CloudinaryService;
import com.project.final_project.common.global.HttpResponseEntity.ResponseResult;
import com.project.final_project.gallery.dto.CloudinaryGalleryRegisterDTO;
import com.project.final_project.gallery.dto.CloudinaryGalleryResponseDTO;
import com.project.final_project.gallery.dto.CloudinaryGalleryUpdateDTO;
import com.project.final_project.gallery.service.GalleryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/gallery")
public class GalleryController {

  private final GalleryService galleryService;
//  private final CloudinaryService cloudinaryService;

  //== Cloudinary ==//
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
//
  @Operation(summary = "갤러리에 아카이빙 등록 - cloudinary 버전",
        description = "갤러리에 아카이빙을 등록합니다.")
  @PostMapping
  public ResponseResult<CloudinaryGalleryResponseDTO> registerCloudinaryGallery(
      @RequestBody CloudinaryGalleryRegisterDTO dto){
    return success(galleryService.registerCloudinaryGallery(dto));
  }

  @GetMapping("/list/{schoolId}")
  public List<CloudinaryGalleryResponseDTO> getCloudinaryGalleryListBySchoolId(@PathVariable("schoolId") Integer schoolId) {
    return galleryService.getCloudinaryGalleryListBySchoolId(schoolId);
  }

  @GetMapping("/list")
  public List<CloudinaryGalleryResponseDTO> getAllCloudinaryGalleries() {
    return galleryService.getAllCloudinaryGalleries();
  }

  @GetMapping("/{galleryId}")
  public CloudinaryGalleryResponseDTO getCloudinaryGalleryById(@PathVariable("galleryId") Integer galleryId){
    return galleryService.getCloudinaryGalleryById(galleryId);
  }

  @PatchMapping
  public ResponseResult<CloudinaryGalleryResponseDTO> updateCloudinaryGallery(
      @RequestBody CloudinaryGalleryUpdateDTO dto) {
    return success(galleryService.updateCloudinaryGallery(dto));
  }


//  @DeleteMapping
//  public ResponseResult<String> removeCloudinaryGallery(@RequestParam("publicId") String publicId) {
//    galleryService.cloudinaryDelete(publicId);
//    return success("remove success publicId : " + publicId);
//  }

}
