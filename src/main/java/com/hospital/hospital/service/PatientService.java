package com.hospital.hospital.service;

import com.hospital.hospital.model.Patient;
import com.hospital.hospital.repo.DistrictRepository;
import com.hospital.hospital.repo.PatientRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

  private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

  private final PatientRepository patientRepo;
  private final DistrictRepository districtRepo;

  @Autowired
  public PatientService(PatientRepository patientRepo, DistrictRepository districtRepo) {
    this.patientRepo = patientRepo;
    this.districtRepo = districtRepo;
    logger.info("Patient Service создан");
  }

  public List<Patient> getAllPatients() {
    logger.info("Запрос на получение списка всех пациентов");
    return patientRepo.findAll();
  }

  public Patient createPatient(String lastName, String firstName, Integer districtId) {
    logger.info("Создание нового пациента: Фамилия={}, Имя={}, Район ID={}",
        lastName, firstName, districtId);
    Patient patient = new Patient();
    patient.setLastName(lastName);
    patient.setFirstName(firstName);
    patient.setDistrict(districtRepo.findById(districtId).orElseThrow());
    return patientRepo.save(patient);
  }

  public Patient getPatientById(Integer id) {
    logger.info("Запрос пациента по ID={}", id);
    return patientRepo.findById(id).orElseThrow();
  }
}
