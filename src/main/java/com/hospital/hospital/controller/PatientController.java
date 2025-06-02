package com.hospital.hospital.controller;

import com.hospital.hospital.model.DoctorSchedule;
import com.hospital.hospital.model.Patient;
import com.hospital.hospital.service.DistrictService;
import com.hospital.hospital.service.DoctorService;
import com.hospital.hospital.service.PatientService;
import com.hospital.hospital.service.ScheduleService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/patients")
public class PatientController {

  private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

  private final PatientService patientService;
  private final DistrictService districtService;
  private final ScheduleService scheduleService;
  private final DoctorService doctorService;

  @Autowired
  public PatientController(final PatientService patientService, final DistrictService districtService,
      final ScheduleService scheduleService, final DoctorService doctorService) {
    this.patientService = patientService;
    this.districtService = districtService;
    this.scheduleService = scheduleService;
    this.doctorService = doctorService;
    logger.info("Patient Controller создан");
  }

  @GetMapping
  public final ModelAndView listPatients() {

    logger.debug("Запрос на получение списка всех пациентов");
    ModelAndView mav = new ModelAndView("patients");
    mav.addObject("patients", patientService.getAllPatients());
    return mav;
  }

  @GetMapping("/new")
  public final ModelAndView newPatientForm() {
    logger.debug("Отображение формы создания нового пациента");
    ModelAndView mav = new ModelAndView("patient-form");
    mav.addObject("districts", districtService.getAllDistricts());
    return mav;
  }

  @PostMapping
  public final String createPatient(@RequestParam final String lastName,
      @RequestParam final String firstName,
      @RequestParam final Integer districtId) {
    logger.info("Создание нового пациента: Фамилия={}, Имя={}, Район ID={}",
        lastName, firstName, districtId);
    patientService.createPatient(lastName, firstName, districtId);
    return "redirect:/patients";
  }

  @GetMapping("/{id}/appointments")
  public final ModelAndView viewPatientAppointments(@PathVariable final Integer id) {
    logger.debug("Запрос записей пациента ID={}", id);
    ModelAndView mav = new ModelAndView("patients");
    Patient patient = patientService.getPatientById(id);
    List<DoctorSchedule> appointments = scheduleService.getPatientAppointments(patient);
    mav.addObject("patient", patient);
    mav.addObject("appointments", appointments);
    return mav;
  }
  @GetMapping("/{id}/book")
  public final ModelAndView showBookingForm(@PathVariable final Integer id) {
    logger.debug("Отображение формы записи для пациента ID={}", id);
    ModelAndView mav = new ModelAndView("book-appointment");
    mav.addObject("patient", patientService.getPatientById(id));
    mav.addObject("therapists", doctorService.getAvailableTherapistsForPatient(id));
    mav.addObject("specialists", doctorService.getSpecialists());
    return mav;
  }
}