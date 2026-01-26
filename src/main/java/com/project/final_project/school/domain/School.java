package com.project.final_project.school.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.final_project.furniture.domain.Furniture;
import com.project.final_project.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "school")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class School {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "school_name")
  private String schoolName;

  @Column(name = "school_location")
  private String location;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "latitude")
  private Double latitude;

  @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<User> userList = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL)
  private List<Furniture> furnitureList = new ArrayList<>();

  public School(String schoolName, String location, Double longitude, Double latitude) {
    this.schoolName = schoolName;
    this.location = location;
    this.longitude = longitude;
    this.latitude = latitude;
  }
}
