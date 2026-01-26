package com.project.final_project.cloudinary.controller;

import com.project.final_project.cloudinary.dto.CloudinaryResponseDTO;
import com.project.final_project.cloudinary.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

//@RestController
//@RequestMapping("/cloudinary")
public class CloudinaryController {

//  private final CloudinaryService cloudinaryService;
//
//  @Autowired
//  public CloudinaryController(CloudinaryService cloudinaryService) {
//    this.cloudinaryService = cloudinaryService;
//  }
//
//  // 이미지 업로드
//  @PostMapping(
//      value = "/upload",
//      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
//      produces = MediaType.APPLICATION_JSON_VALUE
//  )
//  public ResponseEntity<?> uploadImage(
//      @RequestPart("file")
//      @Parameter(description = "업로드할 파일") MultipartFile file) {
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
//  // 이미지 삭제
//  @DeleteMapping("/delete")
//  public ResponseEntity<Map<String, Object>> deleteImage(@RequestParam("publicId") String publicId) {
//    try {
//      Map<String, Object> deleteResult = cloudinaryService.deleteImage(publicId);
//      return ResponseEntity.ok(deleteResult);
//    } catch (IOException e) {
//      return ResponseEntity.status(500).body(Map.of("error", "Image deletion failed"));
//    }
//  }
}
