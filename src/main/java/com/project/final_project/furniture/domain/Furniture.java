package com.project.final_project.furniture.domain;
import com.project.final_project.furniture.dto.FurnitureDTO;
import com.project.final_project.furniture.dto.FurnitureRegisterDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "furniture")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Furniture {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "obj_id")
  private Integer objId;

  @Column(name = "x")
  private Integer x;

  @Column(name = "y")
  private Integer y;

  @Column(name = "rotation")
  private Integer rot;

  @Column(name = "flip")
  private Boolean flip;

  @Column(name = "map_id")
  private Integer mapId;

  // 교실인지 학교인지 타입 구분 데이터 추가
  @Column(name = "map_type")
  private String mapType;

  @Column(name = "user_id")
  private Integer userId;

  // 복사 생성자 (다른 Furniture 객체를 사용)
  public Furniture(Furniture other) {
    this.objId = other.objId;
    this.x = other.x;
    this.y = other.y;
    this.rot = other.rot;
    this.flip = other.flip;
    this.mapId = other.mapId;
    this.mapType = other.mapType;
  }

  // 복사 생성자 (FurnitureRegisterDTO를 사용)
  public Furniture(FurnitureRegisterDTO dto) {
    this.id = null;
    this.objId = dto.getObjId();
    this.x = dto.getX();
    this.y = dto.getY();
    this.rot = dto.getRot();
    this.flip = dto.getFlip();
    this.mapId = dto.getMapId();
    this.mapType = dto.getMapType();
  }

  public Furniture(FurnitureDTO furniture) {
    this.id = furniture.getId();
    this.objId = furniture.getObjId();
    this.x = furniture.getX();
    this.y = furniture.getY();
    this.rot = furniture.getRot();
    this.flip = furniture.getFlip();
    this.mapId = furniture.getMapId();
    this.mapType = furniture.getMapType();
  }


  @Override
  public String toString() {
    return "Furniture{" +
        "id=" + id +
        ", objId=" + objId +
        ", x=" + x +
        ", y=" + y +
        ", rot=" + rot +
        ", flip=" + flip +
        ", mapId=" + mapId +
        ", mapType='" + mapType + '\'' +
        '}';
  }
}