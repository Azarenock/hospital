package com.hospital.hospital;

import com.hospital.hospital.controller.PatientController;
import com.hospital.hospital.model.DoctorSchedule;
import com.hospital.hospital.model.Patient;
import com.hospital.hospital.service.DistrictService;
import com.hospital.hospital.service.DoctorService;
import com.hospital.hospital.service.PatientService;
import com.hospital.hospital.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

  @Mock
  private PatientService patientService;

  @Mock
  private DistrictService districtService;

  @Mock
  private ScheduleService scheduleService;

  @Mock
  private DoctorService doctorService;

  @InjectMocks
  private PatientController patientController;

  private MockMvc mockMvc;

  private Patient testPatient;
  private DoctorSchedule testAppointment;

  @BeforeEach
  public void setUp() {
    // Настраиваем разрешение представлений
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/views/");
    viewResolver.setSuffix(".jsp");

    mockMvc = MockMvcBuilders.standaloneSetup(patientController)
        .setViewResolvers(viewResolver)
        .build();

    testPatient = new Patient();
    testPatient.setPatientId(1);
    testPatient.setFirstName("Иван");
    testPatient.setLastName("Иванов");

    testAppointment = new DoctorSchedule();
    testAppointment.setScheduleId(1);
  }

  @Test
  public void listPatients_ShouldReturnPatientsView() throws Exception {
    when(patientService.getAllPatients()).thenReturn(Collections.singletonList(testPatient));

    mockMvc.perform(get("/patients"))
        .andExpect(status().isOk())
        .andExpect(view().name("patients"))
        .andExpect(model().attributeExists("patients"));

    verify(patientService).getAllPatients();
  }

  @Test
  public void newPatientForm_ShouldReturnPatientFormView() throws Exception {
    when(districtService.getAllDistricts()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/patients/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("patient-form"))
        .andExpect(model().attributeExists("districts"));

    verify(districtService).getAllDistricts();
  }

  @Test
  void createPatient_ShouldRedirectToList() throws Exception {
    when(patientService.createPatient(anyString(), anyString(), anyInt()))
        .thenReturn(testPatient);

    mockMvc.perform(post("/patients")
            .param("lastName", "Иванов")
            .param("firstName", "Иван")
            .param("districtId", "1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/patients"));

    verify(patientService).createPatient("Иванов", "Иван", 1);
  }

  @Test
  public void viewPatientAppointments_ShouldReturnAppointmentsView() throws Exception {
    when(patientService.getPatientById(1)).thenReturn(testPatient);
    when(scheduleService.getPatientAppointments(testPatient))
        .thenReturn(Collections.singletonList(testAppointment));

    mockMvc.perform(get("/patients/1/appointments"))
        .andExpect(status().isOk())
        .andExpect(view().name("patients"))
        .andExpect(model().attributeExists("patient"))
        .andExpect(model().attributeExists("appointments"));

    verify(patientService).getPatientById(1);
    verify(scheduleService).getPatientAppointments(testPatient);
  }

  @Test
  public void showBookingForm_ShouldReturnBookingFormView() throws Exception {
    when(patientService.getPatientById(1)).thenReturn(testPatient);
    when(doctorService.getAvailableTherapistsForPatient(1)).thenReturn(Collections.emptyList());
    when(doctorService.getSpecialists()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/patients/1/book"))
        .andExpect(status().isOk())
        .andExpect(view().name("book-appointment"))
        .andExpect(model().attributeExists("patient"))
        .andExpect(model().attributeExists("therapists"))
        .andExpect(model().attributeExists("specialists"));

    verify(patientService).getPatientById(1);
    verify(doctorService).getAvailableTherapistsForPatient(1);
    verify(doctorService).getSpecialists();
  }
}