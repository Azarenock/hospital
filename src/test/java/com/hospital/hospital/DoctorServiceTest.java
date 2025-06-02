package com.hospital.hospital;

import com.hospital.hospital.model.District;
import com.hospital.hospital.model.Doctor;
import com.hospital.hospital.model.Patient;
import com.hospital.hospital.repo.DoctorRepository;
import com.hospital.hospital.repo.DoctorScheduleRepository;
import com.hospital.hospital.repo.PatientRepository;
import com.hospital.hospital.service.DoctorService;
import com.hospital.hospital.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

  @Mock
  private DoctorRepository doctorRepo;

  @Mock
  private DoctorScheduleRepository scheduleRepo;

  @Mock
  private PatientRepository patientRepo;

  @Mock
  private ScheduleService scheduleService;

  @InjectMocks
  private DoctorService doctorService;

  private Doctor therapist1;
  private Doctor therapist2;
  private Doctor specialist1;
  private Patient patient;
  private District district;

  @BeforeEach
  public void setUp() {
    district = new District();
    district.setDistrictId(1);
    district.setDistrictName("Центральный");

    therapist1 = new Doctor();
    therapist1.setDoctorId(1);
    therapist1.setFirstName("Иван");
    therapist1.setLastName("Горелов");
    therapist1.setTherapist(true);
    therapist1.setDistrict(district);

    therapist2 = new Doctor();
    therapist2.setDoctorId(2);
    therapist2.setFirstName("Жанна");
    therapist2.setLastName("Иванова");
    therapist2.setTherapist(true);
    therapist2.setDistrict(null);

    specialist1 = new Doctor();
    specialist1.setDoctorId(3);
    specialist1.setFirstName("Михаил");
    specialist1.setLastName("Светов");
    specialist1.setTherapist(false);

    patient = new Patient();
    patient.setPatientId(1);
    patient.setFirstName("Пациент");
    patient.setLastName("Тестовый");
    patient.setDistrict(district);
  }

  @Test
  public void getAllDoctors_ShouldReturnAllDoctors() {

    List<Doctor> expectedDoctors = Arrays.asList(therapist1, therapist2, specialist1);
    when(doctorRepo.findAll()).thenReturn(expectedDoctors);

    List<Doctor> actualDoctors = doctorService.getAllDoctors();

    assertEquals(3, actualDoctors.size());
    assertEquals(expectedDoctors, actualDoctors);
    verify(doctorRepo, times(1)).findAll();
  }

  @Test
  public void getAllDoctors_ShouldReturnEmptyListWhenNoDoctors() {

    when(doctorRepo.findAll()).thenReturn(Collections.emptyList());

    List<Doctor> actualDoctors = doctorService.getAllDoctors();

    assertTrue(actualDoctors.isEmpty());
    verify(doctorRepo, times(1)).findAll();
  }

  @Test
  public void getSpecialists_ShouldReturnOnlySpecialists() {

    List<Doctor> expectedSpecialists = Collections.singletonList(specialist1);
    when(doctorRepo.findByIsTherapist(false)).thenReturn(expectedSpecialists);

    List<Doctor> actualSpecialists = doctorService.getSpecialists();

    assertEquals(1, actualSpecialists.size());
    assertEquals(specialist1, actualSpecialists.get(0));
    verify(doctorRepo, times(1)).findByIsTherapist(false);
  }

  @Test
  public void getDoctorById_ShouldReturnDoctorWhenExists() {

    when(doctorRepo.findById(1)).thenReturn(Optional.of(therapist1));

    Doctor foundDoctor = doctorService.getDoctorById(1);

    assertNotNull(foundDoctor);
    assertEquals(therapist1, foundDoctor);
    verify(doctorRepo, times(1)).findById(1);
  }

  @Test
  public void getDoctorById_ShouldThrowExceptionWhenDoctorNotFound() {

    when(doctorRepo.findById(99)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
        doctorService.getDoctorById(99));

    assertEquals("Doctor not found with id: 99", exception.getMessage());
    verify(doctorRepo, times(1)).findById(99);
  }

  @Test
  public void getAvailableTherapistsForPatient_ShouldThrowWhenPatientNotFound() {

    when(patientRepo.findById(99)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
        doctorService.getAvailableTherapistsForPatient(99));

    verify(patientRepo, times(1)).findById(99);
    verify(doctorRepo, never()).findByIsTherapistAndDistrict(anyBoolean(), any());
  }

  @Test
  public void generateInitialScheduleForDoctor_ShouldGenerateSchedule() {

    LocalDate startDate = LocalDate.now();
    when(doctorRepo.findById(1)).thenReturn(Optional.of(therapist1));

    doctorService.generateInitialScheduleForDoctor(1, startDate);

    verify(doctorRepo, times(1)).findById(1);
    verify(scheduleService, times(1)).generateDefaultSchedule(therapist1, startDate, 30);
  }

  @Test
  public void generateInitialScheduleForDoctor_ShouldThrowWhenDoctorNotFound() {

    when(doctorRepo.findById(99)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
        doctorService.generateInitialScheduleForDoctor(99, LocalDate.now()));

    verify(doctorRepo, times(1)).findById(99);
    verify(scheduleService, never()).generateDefaultSchedule(any(), any(), anyInt());
  }
}