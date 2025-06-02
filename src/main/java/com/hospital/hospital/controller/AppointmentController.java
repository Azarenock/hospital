package com.hospital.hospital.controller;

import com.hospital.hospital.model.Doctor;
import com.hospital.hospital.model.Patient;
import com.hospital.hospital.service.DoctorService;
import com.hospital.hospital.service.PatientService;
import com.hospital.hospital.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

  private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

  private final PatientService patientService;
  private final DoctorService doctorService;
  private final ScheduleService scheduleService;

  @Autowired
  public AppointmentController(final PatientService patientService, final DoctorService doctorService,
      final ScheduleService scheduleService) {
    this.patientService = patientService;
    this.doctorService = doctorService;
    this.scheduleService = scheduleService;
    logger.info("Appointment Controller создан");
  }

  @GetMapping("/book")
  public final ModelAndView showBookingForm(@RequestParam final Integer patientId) {
    logger.debug("Отображение формы записи для пациента с ID: {}", patientId);
    ModelAndView mav = new ModelAndView("book-appointment");
    mav.addObject("patient", patientService.getPatientById(patientId));
    mav.addObject("doctors", doctorService.getAllDoctors());
    mav.addObject("currentDate", LocalDate.now());
    return mav;
  }

  @PostMapping("/book/select-doctor")
  public final ModelAndView handleDoctorSelection(
    @RequestParam final Integer patientId,
    @RequestParam final Integer doctorId,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

  if (date == null) {
    date = LocalDate.now();
  }
  logger.info("Обработка выбора врача. Пациент ID: {}, Врач ID: {}, Дата: {}", patientId, doctorId, date);

  final Doctor doctor = doctorService.getDoctorById(doctorId);

  ModelAndView mav = new ModelAndView("book-appointment");
  mav.addObject("patient", patientService.getPatientById(patientId));
  mav.addObject("doctors", doctorService.getAllDoctors());
  mav.addObject("selectedDoctor", doctor);
  mav.addObject("availableSlots", scheduleService.findAvailableSlots(doctor, date));
  mav.addObject("selectedDate", date);
  mav.addObject("currentDate", LocalDate.now());

  return mav;
  }

  @PostMapping("/book/confirm")
  public final String confirmAppointment(
      @RequestParam final Integer patientId,
      @RequestParam final Integer scheduleId,
      RedirectAttributes redirectAttributes) {

    logger.info("Подтверждение записи. Пациент ID: {}, Слот ID: {}", patientId, scheduleId);

    try {
      Patient patient = patientService.getPatientById(patientId);
      if (patient == null) {
        redirectAttributes.addFlashAttribute("error", "Пациент не найден");
        return "redirect:/patients";
      }

      scheduleService.bookAppointment(scheduleId, patient);
      redirectAttributes.addFlashAttribute("successMessage",
          "Пациент " + patient.getLastName() + " " + patient.getFirstName() +
              " успешно записан на прием");

    } catch (EntityNotFoundException e) {
      redirectAttributes.addFlashAttribute("error", "Временной слот не найден");
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return "redirect:/patients";
  }
}