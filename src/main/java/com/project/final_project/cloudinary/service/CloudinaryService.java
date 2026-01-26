package com.project.final_project.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

//@Service
public class CloudinaryService {

//  private final Cloudinary cloudinary;
//
//  @Autowired
//  public CloudinaryService(Cloudinary cloudinary) {
//    this.cloudinary = cloudinary;
//  }
//
//  // 이미지 업로드
//  public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
//    return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
//  }
//
//  // 이미지 삭제
//  public Map<String, Object> deleteImage(String publicId) throws IOException {
//    return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//  }
}
