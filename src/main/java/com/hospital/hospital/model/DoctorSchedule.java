package com.hospital.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "doctor_schedule")
public class DoctorSchedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer scheduleId;

  @ManyToOne
  @JoinColumn(name = "doctor_id", nullable = false)
  private Doctor doctor;

  @Column(nullable = false)
  private LocalDate workDate;

  @Column(nullable = false)
  private LocalTime timeSlot;

  @Column(nullable = false)
  private Boolean isAvailable = true;

  @ManyToOne
  @JoinColumn(name = "patient_id")
  private Patient patient;

  public Boolean getAvailable() {
    return isAvailable;
  }

  public void setAvailable(Boolean available) {
    isAvailable = available;
  }

  public Integer getScheduleId() {
    return scheduleId;
  }

  public void setScheduleId(Integer scheduleId) {
    this.scheduleId = scheduleId;
  }

  public Doctor getDoctor() {
    return doctor;
  }

  public void setDoctor(Doctor doctor) {
    this.doctor = doctor;
  }

  public LocalDate getWorkDate() {
    return workDate;
  }

  public void setWorkDate(LocalDate workDate) {
    this.workDate = workDate;
  }

  public LocalTime getTimeSlot() {
    return timeSlot;
  }

  public void setTimeSlot(LocalTime timeSlot) {
    this.timeSlot = timeSlot;
  }

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DoctorSchedule that = (DoctorSchedule) o;
    return Objects.equals(scheduleId, that.scheduleId) && Objects.equals(doctor,
        that.doctor) && Objects.equals(workDate, that.workDate) && Objects.equals(
        timeSlot, that.timeSlot) && Objects.equals(isAvailable, that.isAvailable)
        && Objects.equals(patient, that.patient);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scheduleId, doctor, workDate, timeSlot, isAvailable, patient);
  }

  @Override
  public String toString() {
    return "DoctorSchedule{" +
        "scheduleId=" + scheduleId +
        ", doctor=" + doctor +
        ", workDate=" + workDate +
        ", timeSlot=" + timeSlot +
        ", isAvailable=" + isAvailable +
        ", patient=" + patient +
        '}';
  }
}
