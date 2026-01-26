package com.project.final_project.gallery.service;

import com.project.final_project.cloudinary.service.CloudinaryService;
import com.project.final_project.gallery.domain.Gallery;
import com.project.final_project.gallery.dto.CloudinaryGalleryRegisterDTO;
import com.project.final_project.gallery.dto.CloudinaryGalleryResponseDTO;
import com.project.final_project.gallery.dto.CloudinaryGalleryUpdateDTO;
import com.project.final_project.gallery.repository.GalleryRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GalleryService {

  private final GalleryRepository galleryRepository;
//  private final CloudinaryService cloudinaryService;


  public CloudinaryGalleryResponseDTO registerCloudinaryGallery(CloudinaryGalleryRegisterDTO dto) {
    Gallery gallery
        = new Gallery(
            dto.getImgUrl(), dto.getTitle(), dto.getSchoolId(), dto.getUserId(), dto.getPublicId());
    return new CloudinaryGalleryResponseDTO(galleryRepository.save(gallery));
  }

  public List<CloudinaryGalleryResponseDTO> getAllCloudinaryGalleries() {
    return galleryRepository.findAll().stream().map(CloudinaryGalleryResponseDTO::new).toList();
  }

  public CloudinaryGalleryResponseDTO getCloudinaryGalleryById(Integer galleryId) {
    return new CloudinaryGalleryResponseDTO(galleryRepository.findById(galleryId).orElseThrow(
        () -> new IllegalStateException("not found gallery id : " + galleryId)));
  }

  public List<CloudinaryGalleryResponseDTO> getCloudinaryGalleryListBySchoolId(Integer schoolId) {
    return galleryRepository.
        getGalleryListBySchoolId(schoolId).stream().map(CloudinaryGalleryResponseDTO::new).toList();
  }

//  @Transactional
//  public void deleteGalleryByGalleryId(Integer galleryId) {
//    Gallery gallery = galleryRepository.findById(galleryId).orElseThrow(
//        () -> new IllegalStateException("not found gallery id : " + galleryId)
//    );
//    cloudinaryDelete(gallery.getPublicId());
//    galleryRepository.delete(gallery);
//  }

//  @Transactional
//  public void deleteGalleryListByUserId(Integer userId) {
//    galleryRepository.getGalleryListByUserId(userId).forEach(
//        g -> {
//          cloudinaryDelete(g.getPublicId());
//          galleryRepository.delete(g);
//        }
//    );
//  }

//  public void cloudinaryDelete(String publicId) {
//    try {
//      cloudinaryService.deleteImage(publicId);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }

  public CloudinaryGalleryResponseDTO updateCloudinaryGallery(CloudinaryGalleryUpdateDTO dto) {
    Gallery gallery = galleryRepository.findById(dto.getGalleryId()).orElseThrow(
    () -> new IllegalStateException("not found Gallery id : " + dto.getGalleryId()));

    if(dto.getImgUrl() != null){
      gallery.setImgUrl(dto.getImgUrl());
    }

    if(dto.getTitle() != null) {
      gallery.setTitle(dto.getTitle());
    }

    if(dto.getEnteredDate() != null) {
      gallery.setEnteredDate(dto.getEnteredDate());
    }

    return new CloudinaryGalleryResponseDTO(gallery);
  }
}
