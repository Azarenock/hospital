package com.hospital.hospital.repo;

import com.hospital.hospital.model.Doctor;
import com.hospital.hospital.model.DoctorSchedule;
import com.hospital.hospital.model.Patient;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Integer> {

  List<DoctorSchedule> findByPatient(Patient patient);

  void deleteByDoctorAndWorkDateBetween(Doctor doctor, LocalDate start, LocalDate end);

  List<DoctorSchedule> findByDoctorAndIsAvailableAndWorkDate(
      Doctor doctor, boolean isAvailable, LocalDate workDate);

  List<DoctorSchedule> findByDoctor_DoctorIdAndWorkDateBetweenOrderByWorkDateAscTimeSlotAsc(
      Integer doctorId, LocalDate startDate, LocalDate endDate);
}
