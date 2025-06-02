package com.hospital.hospital;

import com.hospital.hospital.model.Doctor;
import com.hospital.hospital.model.DoctorSchedule;
import com.hospital.hospital.model.Patient;
import com.hospital.hospital.repo.DoctorScheduleRepository;
import com.hospital.hospital.repo.PatientRepository;
import com.hospital.hospital.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

  @Mock
  private DoctorScheduleRepository scheduleRepo;

  @Mock
  private PatientRepository patientRepo;

  @InjectMocks
  private ScheduleService scheduleService;

  private Doctor doctor;
  private Patient patient;
  private DoctorSchedule availableSlot;
  private DoctorSchedule bookedSlot;
  private LocalDate testDate;

  @BeforeEach
  public void setUp() {
    testDate = LocalDate.of(2023, 6, 1); // Четверг
    doctor = new Doctor();
    doctor.setDoctorId(1);
    doctor.setWorkSchedule("утро пн-ср-пт");

    patient = new Patient();
    patient.setPatientId(1);

    availableSlot = new DoctorSchedule();
    availableSlot.setScheduleId(1);
    availableSlot.setDoctor(doctor);
    availableSlot.setWorkDate(testDate);
    availableSlot.setTimeSlot(LocalTime.of(9, 0));
    availableSlot.setAvailable(true);

    bookedSlot = new DoctorSchedule();
    bookedSlot.setScheduleId(2);
    bookedSlot.setDoctor(doctor);
    bookedSlot.setWorkDate(testDate);
    bookedSlot.setTimeSlot(LocalTime.of(10, 0));
    bookedSlot.setAvailable(false);
    bookedSlot.setPatient(patient);
  }

  @Test
  public void bookAppointment_ShouldBookAvailableSlot() {
    when(scheduleRepo.findById(1)).thenReturn(Optional.of(availableSlot));
    when(scheduleRepo.save(any(DoctorSchedule.class))).thenReturn(availableSlot);

    scheduleService.bookAppointment(1, patient);

    assertFalse(availableSlot.getAvailable());
    assertEquals(patient, availableSlot.getPatient());
    verify(scheduleRepo).save(availableSlot);
  }

  @Test
  public void bookAppointment_ShouldThrowWhenSlotNotFound() {
    when(scheduleRepo.findById(99)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
        scheduleService.bookAppointment(99, patient));
  }

  @Test
  public void bookAppointment_ShouldThrowWhenSlotAlreadyBooked() {
    when(scheduleRepo.findById(2)).thenReturn(Optional.of(bookedSlot));

    assertThrows(IllegalStateException.class, () ->
        scheduleService.bookAppointment(2, patient));
  }

  @Test
  public void cancelAppointment_ShouldCancelBookedSlot() {
    when(scheduleRepo.findById(2)).thenReturn(Optional.of(bookedSlot));
    when(scheduleRepo.save(any(DoctorSchedule.class))).thenReturn(bookedSlot);

    scheduleService.cancelAppointment(2);

    assertTrue(bookedSlot.getAvailable());
    assertNull(bookedSlot.getPatient());
    verify(scheduleRepo).save(bookedSlot);
  }

  @Test
  public void cancelAppointment_ShouldThrowWhenSlotNotFound() {
    when(scheduleRepo.findById(99)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
        scheduleService.cancelAppointment(99));
  }

  @Test
  public void findAvailableSlots_ShouldReturnAvailableSlots() {
    when(scheduleRepo.findByDoctorAndIsAvailableAndWorkDate(doctor, true, testDate))
        .thenReturn(Collections.singletonList(availableSlot));

    List<DoctorSchedule> result = scheduleService.findAvailableSlots(doctor, testDate);

    assertEquals(1, result.size());
    assertEquals(availableSlot, result.get(0));
  }

  @Test
  public void findAvailableSlots_ShouldThrowWhenNullInput() {
    assertThrows(IllegalArgumentException.class, () ->
        scheduleService.findAvailableSlots(null, testDate));

    assertThrows(IllegalArgumentException.class, () ->
        scheduleService.findAvailableSlots(doctor, null));
  }

  @Test
  public void getPatientAppointments_ShouldReturnPatientAppointments() {
    when(scheduleRepo.findByPatient(patient))
        .thenReturn(Collections.singletonList(bookedSlot));

    List<DoctorSchedule> result = scheduleService.getPatientAppointments(patient);

    assertEquals(1, result.size());
    assertEquals(bookedSlot, result.get(0));
  }

  @Test
  public void getAppointmentById_ShouldReturnAppointment() {
    when(scheduleRepo.findById(1)).thenReturn(Optional.of(availableSlot));

    DoctorSchedule result = scheduleService.getAppointmentById(1);

    assertEquals(availableSlot, result);
  }

  @Test
  public void getAppointmentById_ShouldThrowWhenNotFound() {
    when(scheduleRepo.findById(99)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
        scheduleService.getAppointmentById(99));
  }

  @Test
  public void findByDoctorAndDateRange_ShouldReturnSlotsInRange() {
    LocalDate startDate = LocalDate.of(2023, 6, 1);
    LocalDate endDate = LocalDate.of(2023, 6, 7);
    List<DoctorSchedule> expectedSlots = Arrays.asList(availableSlot, bookedSlot);

    when(scheduleRepo.findByDoctor_DoctorIdAndWorkDateBetweenOrderByWorkDateAscTimeSlotAsc(1, startDate, endDate))
        .thenReturn(expectedSlots);

    List<DoctorSchedule> result = scheduleService.findByDoctorAndDateRange(1, startDate, endDate);

    assertEquals(2, result.size());
    assertEquals(expectedSlots, result);
  }

}