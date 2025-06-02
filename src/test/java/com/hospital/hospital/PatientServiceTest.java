package com.hospital.hospital;

import com.hospital.hospital.model.District;
import com.hospital.hospital.model.Patient;
import com.hospital.hospital.repo.DistrictRepository;
import com.hospital.hospital.repo.PatientRepository;
import com.hospital.hospital.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

  @Mock
  private PatientRepository patientRepo;

  @Mock
  private DistrictRepository districtRepo;

  @InjectMocks
  private PatientService patientService;

  private Patient patient1;
  private Patient patient2;
  private District district;

  @BeforeEach
  public void setUp() {
    district = new District();
    district.setDistrictId(1);
    district.setDistrictName("Центральный");

    patient1 = new Patient();
    patient1.setPatientId(1);
    patient1.setFirstName("Иван");
    patient1.setLastName("Немой");
    patient1.setDistrict(district);

    patient2 = new Patient();
    patient2.setPatientId(2);
    patient2.setFirstName("Юлия");
    patient2.setLastName("Иванова");
    patient2.setDistrict(district);
  }

  @Test
  public void getAllPatients_ShouldReturnAllPatients() {

    List<Patient> expectedPatients = Arrays.asList(patient1, patient2);
    when(patientRepo.findAll()).thenReturn(expectedPatients);

    List<Patient> actualPatients = patientService.getAllPatients();

    assertEquals(2, actualPatients.size());
    assertEquals(expectedPatients, actualPatients);
    verify(patientRepo, times(1)).findAll();
  }

  @Test
  public void getAllPatients_ShouldReturnEmptyListWhenNoPatients() {

    when(patientRepo.findAll()).thenReturn(List.of());

    List<Patient> actualPatients = patientService.getAllPatients();

    assertTrue(actualPatients.isEmpty());
    verify(patientRepo, times(1)).findAll();
  }

  @Test
  public void createPatient_ShouldCreateNewPatient() {

    when(districtRepo.findById(1)).thenReturn(Optional.of(district));
    when(patientRepo.save(any(Patient.class))).thenReturn(patient1);

    Patient createdPatient = patientService.createPatient("Немой", "Иван", 1);

    assertNotNull(createdPatient);
    assertEquals("Иван", createdPatient.getFirstName());
    assertEquals("Немой", createdPatient.getLastName());
    assertEquals(district, createdPatient.getDistrict());
    verify(districtRepo, times(1)).findById(1);
    verify(patientRepo, times(1)).save(any(Patient.class));
  }

  @Test
  public void createPatient_ShouldThrowExceptionWhenDistrictNotFound() {

    when(districtRepo.findById(99)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () ->
        patientService.createPatient("Немой", "Иван", 99));

    verify(districtRepo, times(1)).findById(99);
    verify(patientRepo, never()).save(any(Patient.class));
  }

  @Test
  public void getPatientById_ShouldReturnPatientWhenExists() {

    when(patientRepo.findById(1)).thenReturn(Optional.of(patient1));

    Patient foundPatient = patientService.getPatientById(1);

    assertNotNull(foundPatient);
    assertEquals(patient1, foundPatient);
    verify(patientRepo, times(1)).findById(1);
  }

  @Test
  public void getPatientById_ShouldThrowExceptionWhenPatientNotFound() {

    when(patientRepo.findById(99)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () ->
        patientService.getPatientById(99));

    verify(patientRepo, times(1)).findById(99);
  }

  @Test
  public void createPatient_ShouldHandleNullNames() {

    when(districtRepo.findById(1)).thenReturn(Optional.of(district));
    when(patientRepo.save(any(Patient.class))).thenAnswer(invocation -> {
      Patient p = invocation.getArgument(0);
      p.setPatientId(3);
      return p;
    });

    Patient createdPatient = patientService.createPatient(null, null, 1);

    assertNotNull(createdPatient);
    assertNull(createdPatient.getFirstName());
    assertNull(createdPatient.getLastName());
    assertEquals(district, createdPatient.getDistrict());
    verify(districtRepo, times(1)).findById(1);
    verify(patientRepo, times(1)).save(any(Patient.class));
  }

  @Test
  public void createPatient_ShouldVerifyDistrictAssociation() {

    District newDistrict = new District();
    newDistrict.setDistrictId(2);
    newDistrict.setDistrictName("Северный");

    when(districtRepo.findById(2)).thenReturn(Optional.of(newDistrict));
    when(patientRepo.save(any(Patient.class))).thenAnswer(invocation -> {
      return invocation.getArgument(0);
    });

    Patient result = patientService.createPatient("Нищий", "Михаил", 2);

    assertNotNull(result);
    assertEquals("Михаил", result.getFirstName());
    assertEquals("Нищий", result.getLastName());
    assertEquals(newDistrict, result.getDistrict());
  }
}