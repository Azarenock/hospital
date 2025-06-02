package com.hospital.hospital.service;

import com.hospital.hospital.model.Doctor;
import com.hospital.hospital.model.Patient;
import com.hospital.hospital.repo.DoctorRepository;
import com.hospital.hospital.repo.DoctorScheduleRepository;
import com.hospital.hospital.repo.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

  private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

  private final DoctorRepository doctorRepo;
  private final DoctorScheduleRepository scheduleRepo;
  private final PatientRepository patientRepo;
  private final ScheduleService scheduleService;

  @Autowired
  public DoctorService(final DoctorRepository doctorRepo,
      final DoctorScheduleRepository scheduleRepo,
      final PatientRepository patientRepo,
      final ScheduleService scheduleService) {
    this.doctorRepo = doctorRepo;
    this.scheduleRepo = scheduleRepo;
    this.patientRepo = patientRepo;
    this.scheduleService = scheduleService;
    logger.info("DoctorService создан");
  }

  public List<Doctor> getAllDoctors() {
    logger.debug("Запрос на получение списка всех врачей");
    return doctorRepo.findAll();
  }

  public List<Doctor> getSpecialists() {
    logger.debug("Запрос на получение списка специалистов");
    return doctorRepo.findByIsTherapist(false);
  }

  public Doctor getDoctorById(Integer id) {
    logger.debug("Поиск врача по ID: {}", id);
    return doctorRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + id));
  }

  public List<Doctor> getAvailableTherapistsForPatient(Integer patientId) {
    logger.debug("Поиск доступных терапевтов для пациента ID: {}", patientId);
    Patient patient = patientRepo.findById(patientId)
        .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

    List<Doctor> districtTherapists = doctorRepo.findByIsTherapistAndDistrict(true, patient.getDistrict());

    List<Doctor> allTherapists = doctorRepo.findByIsTherapist(true);
    List<Doctor> otherTherapists = allTherapists.stream()
        .filter(d -> !d.equals(patient.getDistrict()))
        .collect(Collectors.toList());

    districtTherapists.addAll(otherTherapists);
    return districtTherapists;
  }

  public void generateInitialScheduleForDoctor(Integer doctorId, LocalDate startDate) {
    logger.info("Генерация расписания для врача ID={} начиная с {}", doctorId, startDate);
    Doctor doctor = doctorRepo.findById(doctorId)
        .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
    scheduleService.generateDefaultSchedule(doctor, startDate, 30); // На 30 дней вперед
  }
}