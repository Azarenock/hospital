package com.hospital.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "districts")
public class District {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer districtId;

  @Column(nullable = false)
  private String districtName;

  public Integer getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Integer districtId) {
    this.districtId = districtId;
  }

  public String getDistrictName() {
    return districtName;
  }

  public void setDistrictName(String districtName) {
    this.districtName = districtName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    District district = (District) o;
    return Objects.equals(districtId, district.districtId) && Objects.equals(
        districtName, district.districtName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(districtId, districtName);
  }

  @Override
  public String toString() {
    return "District{" +
        "districtId=" + districtId +
        ", districtName='" + districtName + '\'' +
        '}';
  }
}
