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
@Table(name = "doctors")
public class Doctor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer doctorId;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private Boolean isTherapist;

  @ManyToOne
  @JoinColumn(name = "district_id")
  private District district;

  @Column(nullable = false)
  private String workSchedule;

  public Doctor() {
  }

  public Doctor(Integer doctorId) {
    this.doctorId = doctorId;
  }

  public Integer getDoctorId() {
    return doctorId;
  }

  public void setDoctorId(Integer doctorId) {
    this.doctorId = doctorId;
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

  public Boolean getTherapist() {
    return isTherapist;
  }

  public void setTherapist(Boolean therapist) {
    isTherapist = therapist;
  }

  public District getDistrict() {
    return district;
  }

  public void setDistrict(District district) {
    this.district = district;
  }

  public String getWorkSchedule() {
    return workSchedule;
  }

  public void setWorkSchedule(String workSchedule) {
    this.workSchedule = workSchedule;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Doctor doctor = (Doctor) o;
    return Objects.equals(doctorId, doctor.doctorId) && Objects.equals(lastName,
        doctor.lastName) && Objects.equals(firstName, doctor.firstName)
        && Objects.equals(isTherapist, doctor.isTherapist) && Objects.equals(
        district, doctor.district) && Objects.equals(workSchedule, doctor.workSchedule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(doctorId, lastName, firstName, isTherapist, district, workSchedule);
  }

  @Override
  public String toString() {
    return "Doctor{" +
        "doctorId=" + doctorId +
        ", lastName='" + lastName + '\'' +
        ", firstName='" + firstName + '\'' +
        ", isTherapist=" + isTherapist +
        ", district=" + district +
        ", workSchedule='" + workSchedule + '\'' +
        '}';
  }
}
