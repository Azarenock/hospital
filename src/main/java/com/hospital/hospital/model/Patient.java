package com.hospital.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "patients")
public class Patient {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer patientId;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String firstName;

  @ManyToOne
  @JoinColumn(name = "district_id", nullable = false)
  private District district;


  public Integer getPatientId() {
    return patientId;
  }

  public void setPatientId(Integer patientId) {
    this.patientId = patientId;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public District getDistrict() {
    return district;
  }

  public void setDistrict(District district) {
    this.district = district;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Patient patient = (Patient) o;
    return Objects.equals(patientId, patient.patientId) && Objects.equals(
        lastName, patient.lastName) && Objects.equals(firstName, patient.firstName)
        && Objects.equals(district, patient.district);
  }

  @Override
  public int hashCode() {
    return Objects.hash(patientId, lastName, firstName, district);
  }

  @Override
  public String toString() {
    return "Patient{" +
        "patientId=" + patientId +
        ", lastName='" + lastName + '\'' +
        ", firstName='" + firstName + '\'' +
        ", district=" + district +
        '}';
  }
}
