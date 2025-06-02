package com.hospital.hospital.controller;

import com.hospital.hospital.model.Doctor;
import com.hospital.hospital.service.DoctorService;
import com.hospital.hospital.service.ScheduleService;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

  private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

  private final DoctorService doctorService;
  private final ScheduleService scheduleService;

  @Autowired
  public DoctorController(final DoctorService doctorService, final ScheduleService scheduleService) {
    this.doctorService = doctorService;
    this.scheduleService = scheduleService;
    logger.info("DoctorController создан");
  }

  @GetMapping
  public final ModelAndView getAllDoctors() {
    logger.debug("Запрос на получение списка всех врачей");
    return new ModelAndView("doctors")
        .addObject("doctors", doctorService.getAllDoctors());
  }

  @GetMapping("/{id}/schedule")
  public final ModelAndView viewSchedule(@PathVariable final Integer id,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {

    logger.debug("Запрос расписания для врача ID: {}, месяц: {}", id, month);
    LocalDate startDate = month != null ?
        month.withDayOfMonth(1) :
        LocalDate.now().withDayOfMonth(1);
    LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

    Doctor doctor = doctorService.getDoctorById(id);
    if (doctor == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found");
    }

    return new ModelAndView("doctor-schedule")
        .addObject("doctor", doctor)
        .addObject("schedule", scheduleService.findByDoctorAndDateRange(id, startDate, endDate))
        .addObject("selectedMonth", startDate);
  }

  @PostMapping("/{id}/generate-schedule")
  public final String generateDoctorSchedule(@PathVariable final Integer id,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {

    logger.info("Запрос на генерацию расписания для врача ID: {}, месяц: {}", id, month);
    LocalDate startDate = month != null ?
        month.withDayOfMonth(1) :
        LocalDate.now().withDayOfMonth(1);
    doctorService.generateInitialScheduleForDoctor(id, startDate);

    return "redirect:/doctors/" + id + "/doctor-schedule" +
        (month != null ? "?month=" + month : "");
  }
}