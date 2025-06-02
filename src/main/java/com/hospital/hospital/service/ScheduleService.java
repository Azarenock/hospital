package com.hospital.hospital.service;

import com.hospital.hospital.model.Doctor;
import com.hospital.hospital.model.DoctorSchedule;
import com.hospital.hospital.model.Patient;
import com.hospital.hospital.repo.DoctorScheduleRepository;
import com.hospital.hospital.repo.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

  private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

  private final DoctorScheduleRepository scheduleRepo;
  private final PatientRepository patientRepo;

  @Autowired
  public ScheduleService(final DoctorScheduleRepository scheduleRepo,
      final PatientRepository patientRepo) {
    this.scheduleRepo = scheduleRepo;
    this.patientRepo = patientRepo;
    logger.info("Schedule service создан");
  }

  @Transactional
  public void bookAppointment(Integer scheduleId, Patient patient) {
    logger.info("Запрос на бронирование приема: scheduleId={}, patientId={}",
        scheduleId, patient != null ? patient.getPatientId() : "null");
    DoctorSchedule slot = scheduleRepo.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("Time slot not found"));

    if (!slot.getAvailable()) {
      throw new IllegalStateException("Это время уже занято");
    }

    slot.setPatient(patient);
    slot.setAvailable(false);
    scheduleRepo.save(slot);
  }

  @Transactional
  public void cancelAppointment(Integer scheduleId) {
    logger.info("Запрос на отмену приема: scheduleId={}", scheduleId);
    DoctorSchedule appointment = scheduleRepo.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

    appointment.setPatient(null);
    appointment.setAvailable(true);
    scheduleRepo.save(appointment);
  }

  public List<DoctorSchedule> findAvailableSlots(Doctor doctor, LocalDate date) {
    logger.debug("Поиск доступных слотов: doctorId={}, date={}",
        doctor != null ? doctor.getDoctorId() : "null", date);
    if (doctor == null || date == null) {
      throw new IllegalArgumentException("Doctor and date must not be null");
    }
    return scheduleRepo.findByDoctorAndIsAvailableAndWorkDate(doctor, true, date);
  }

  public List<DoctorSchedule> getPatientAppointments(Patient patient) {
    logger.debug("Получение списка приемов пациента: patientId={}",
        patient != null ? patient.getPatientId() : "null");
    return scheduleRepo.findByPatient(patient);
  }

  public DoctorSchedule getAppointmentById(Integer scheduleId) {
    logger.debug("Получение приема по ID: scheduleId={}", scheduleId);
    return scheduleRepo.findById(scheduleId)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
  }

  @Transactional
  public void generateDefaultSchedule(Doctor doctor, LocalDate startDate, int days) {
    logger.info("Генерация расписания: doctorId={}, startDate={}, days={}",
        doctor.getDoctorId(), startDate, days);
    LocalDate endDate = startDate.plusDays(days);
    scheduleRepo.deleteByDoctorAndWorkDateBetween(doctor, startDate, endDate);

    LocalTime morningStart = LocalTime.of(8, 0);
    LocalTime eveningStart = LocalTime.of(13, 0);

    for (int i = 0; i < days; i++) {
      LocalDate date = startDate.plusDays(i);
      if (isDoctorWorkingDay(doctor, date)) {
        boolean isMorningShift = doctor.getWorkSchedule().contains("утро");
        LocalTime start = isMorningShift ? morningStart : eveningStart;
        LocalTime end = start.plusHours(4);

        while (start.isBefore(end)) {
          DoctorSchedule slot = new DoctorSchedule();
          slot.setDoctor(doctor);
          slot.setWorkDate(date);
          slot.setTimeSlot(start);
          slot.setAvailable(true);
          scheduleRepo.save(slot);

          start = start.plusMinutes(30);
        }
      }
    }
  }

  private boolean isDoctorWorkingDay(Doctor doctor, LocalDate date) {
    String schedule = doctor.getWorkSchedule();
    DayOfWeek dayOfWeek = date.getDayOfWeek();

    if (schedule.contains("пн-ср-пт")) {
      return dayOfWeek == DayOfWeek.MONDAY ||
          dayOfWeek == DayOfWeek.WEDNESDAY ||
          dayOfWeek == DayOfWeek.FRIDAY;
    } else {
      return dayOfWeek == DayOfWeek.TUESDAY ||
          dayOfWeek == DayOfWeek.THURSDAY ||
          dayOfWeek == DayOfWeek.SATURDAY;
    }
  }

  public List<DoctorSchedule> findByDoctorAndDateRange(Integer doctorId, LocalDate startDate, LocalDate endDate) {
    logger.debug("Поиск расписания по диапазону дат: doctorId={}, startDate={}, endDate={}",
        doctorId, startDate, endDate);
    return scheduleRepo.findByDoctor_DoctorIdAndWorkDateBetweenOrderByWorkDateAscTimeSlotAsc(
        doctorId, startDate, endDate);
  }
}