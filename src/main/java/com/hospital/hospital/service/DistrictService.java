package com.hospital.hospital.service;

import com.hospital.hospital.model.District;
import com.hospital.hospital.repo.DistrictRepository;
import com.hospital.hospital.repo.DoctorRepository;
import com.hospital.hospital.repo.PatientRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistrictService {

  private static final Logger logger = LoggerFactory.getLogger(DistrictService.class);

  private final DistrictRepository districtRepo;
  private final DoctorRepository doctorRepo;
  private final PatientRepository patientRepo;

  @Autowired
  public DistrictService(final DistrictRepository districtRepo, final DoctorRepository doctorRepo,
      final PatientRepository patientRepo) {
    this.districtRepo = districtRepo;
    this.doctorRepo = doctorRepo;
    this.patientRepo = patientRepo;
    logger.info("DistrictService создан");
  }

  public List<District> getAllDistricts() {
    logger.debug("Запрос на получение всех районов");
    return districtRepo.findAll();
  }
}
