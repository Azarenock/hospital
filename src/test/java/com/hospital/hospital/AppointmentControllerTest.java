package com.hospital.hospital;

import com.hospital.hospital.controller.AppointmentController;
import com.hospital.hospital.model.Doctor;
import com.hospital.hospital.model.Patient;
import com.hospital.hospital.service.DoctorService;
import com.hospital.hospital.service.PatientService;
import com.hospital.hospital.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

  @Mock
  private PatientService patientService;

  @Mock
  private DoctorService doctorService;

  @Mock
  private ScheduleService scheduleService;

  @InjectMocks
  private AppointmentController appointmentController;

  private MockMvc mockMvc;

  private Patient testPatient;
  private Doctor testDoctor;
  private List<Doctor> testDoctors;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();

    testPatient = new Patient();
    testPatient.setPatientId(1);
    testPatient.setFirstName("Даня");
    testPatient.setLastName("Смирнов");

    testDoctor = new Doctor();
    testDoctor.setDoctorId(1);
    testDoctor.setFirstName("Иван");
    testDoctor.setLastName("Смирнов");

    testDoctors = Collections.singletonList(testDoctor);
  }

  @Test
  void showBookingForm_ShouldReturnBookingFormView() throws Exception {
    when(patientService.getPatientById(1)).thenReturn(testPatient);
    when(doctorService.getAllDoctors()).thenReturn(testDoctors);

    mockMvc.perform(get("/appointments/book").param("patientId", "1"))
        .andExpect(status().isOk())
        .andExpect(view().name("book-appointment"))
        .andExpect(model().attributeExists("patient"))
        .andExpect(model().attributeExists("doctors"))
        .andExpect(model().attributeExists("currentDate"));

    verify(patientService).getPatientById(1);
    verify(doctorService).getAllDoctors();
  }

  @Test
  public void handleDoctorSelection_WithDate_ShouldReturnViewWithAvailableSlots() throws Exception {
    LocalDate testDate = LocalDate.now().plusDays(1);
    when(patientService.getPatientById(1)).thenReturn(testPatient);
    when(doctorService.getAllDoctors()).thenReturn(testDoctors);
    when(doctorService.getDoctorById(1)).thenReturn(testDoctor);
    when(scheduleService.findAvailableSlots(testDoctor, testDate)).thenReturn(Collections.emptyList());

    mockMvc.perform(post("/appointments/book/select-doctor")
            .param("patientId", "1")
            .param("doctorId", "1")
            .param("date", testDate.toString()))
        .andExpect(status().isOk())
        .andExpect(view().name("book-appointment"))
        .andExpect(model().attributeExists("patient"))
        .andExpect(model().attributeExists("doctors"))
        .andExpect(model().attributeExists("selectedDoctor"))
        .andExpect(model().attributeExists("availableSlots"))
        .andExpect(model().attributeExists("selectedDate"))
        .andExpect(model().attributeExists("currentDate"));

    verify(patientService).getPatientById(1);
    verify(doctorService).getAllDoctors();
    verify(doctorService).getDoctorById(1);
    verify(scheduleService).findAvailableSlots(testDoctor, testDate);
  }

  @Test
  public void handleDoctorSelection_WithoutDate_ShouldUseCurrentDate() throws Exception {
    when(patientService.getPatientById(1)).thenReturn(testPatient);
    when(doctorService.getAllDoctors()).thenReturn(testDoctors);
    when(doctorService.getDoctorById(1)).thenReturn(testDoctor);
    when(scheduleService.findAvailableSlots(testDoctor, LocalDate.now())).thenReturn(Collections.emptyList());

    mockMvc.perform(post("/appointments/book/select-doctor")
            .param("patientId", "1")
            .param("doctorId", "1"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("selectedDate", LocalDate.now()));

    verify(scheduleService).findAvailableSlots(testDoctor, LocalDate.now());
  }

  @Test
  public void confirmAppointment_WithValidData_ShouldRedirectWithSuccess() throws Exception {
    when(patientService.getPatientById(1)).thenReturn(testPatient);
    doNothing().when(scheduleService).bookAppointment(1, testPatient);

    mockMvc.perform(post("/appointments/book/confirm")
            .param("patientId", "1")
            .param("scheduleId", "1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/patients"))
        .andExpect(flash().attributeExists("successMessage"));

    verify(patientService).getPatientById(1);
    verify(scheduleService).bookAppointment(1, testPatient);
  }

  @Test
  public void confirmAppointment_WithNonExistentPatient_ShouldRedirectWithError() throws Exception {
    when(patientService.getPatientById(1)).thenReturn(null);

    mockMvc.perform(post("/appointments/book/confirm")
            .param("patientId", "1")
            .param("scheduleId", "1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/patients"))
        .andExpect(flash().attributeExists("error"));

    verify(patientService).getPatientById(1);
    verify(scheduleService, never()).bookAppointment(anyInt(), any());
  }

  @Test
  public void confirmAppointment_WithNonExistentSchedule_ShouldRedirectWithError() throws Exception {
    when(patientService.getPatientById(1)).thenReturn(testPatient);
    doThrow(new EntityNotFoundException()).when(scheduleService).bookAppointment(1, testPatient);

    mockMvc.perform(post("/appointments/book/confirm")
            .param("patientId", "1")
            .param("scheduleId", "1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/patients"))
        .andExpect(flash().attributeExists("error"));

    verify(patientService).getPatientById(1);
    verify(scheduleService).bookAppointment(1, testPatient);
  }

  @Test
  public void confirmAppointment_WithIllegalState_ShouldRedirectWithError() throws Exception {
    when(patientService.getPatientById(1)).thenReturn(testPatient);
    doThrow(new IllegalStateException("Slot already booked")).when(scheduleService).bookAppointment(1, testPatient);

    mockMvc.perform(post("/appointments/book/confirm")
            .param("patientId", "1")
            .param("scheduleId", "1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/patients"))
        .andExpect(flash().attributeExists("error"));

    verify(patientService).getPatientById(1);
    verify(scheduleService).bookAppointment(1, testPatient);
  }
}