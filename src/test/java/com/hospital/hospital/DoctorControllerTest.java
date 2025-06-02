package com.hospital.hospital;

import com.hospital.hospital.controller.DoctorController;
import com.hospital.hospital.model.Doctor;
import com.hospital.hospital.service.DoctorService;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

  @Mock
  private DoctorService doctorService;

  @Mock
  private ScheduleService scheduleService;

  @InjectMocks
  private DoctorController doctorController;

  private MockMvc mockMvc;
  private Doctor testDoctor;

  @BeforeEach
  void setUp() {
    // Настройка разрешения представлений
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/WEB-INF/views/");
    viewResolver.setSuffix(".jsp");

    mockMvc = MockMvcBuilders.standaloneSetup(doctorController)
        .setViewResolvers(viewResolver)
        .build();

    testDoctor = new Doctor();
    testDoctor.setDoctorId(1);
    testDoctor.setFirstName("Иван");
    testDoctor.setLastName("Петров");
  }

  @Test
  public void getAllDoctors_ShouldReturnDoctorsView() throws Exception {
    when(doctorService.getAllDoctors()).thenReturn(Collections.singletonList(testDoctor));

    mockMvc.perform(get("/doctors"))
        .andExpect(status().isOk())
        .andExpect(view().name("doctors"))
        .andExpect(model().attributeExists("doctors"))
        .andExpect(model().attribute("doctors", Collections.singletonList(testDoctor)));

    verify(doctorService).getAllDoctors();
  }

  @Test
  public void viewSchedule_WithCurrentMonth_ShouldReturnScheduleView() throws Exception {
    LocalDate currentDate = LocalDate.now();
    LocalDate startDate = currentDate.withDayOfMonth(1);
    LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

    when(doctorService.getDoctorById(1)).thenReturn(testDoctor);
    when(scheduleService.findByDoctorAndDateRange(1, startDate, endDate))
        .thenReturn(Collections.emptyList());

    mockMvc.perform(get("/doctors/1/schedule"))
        .andExpect(status().isOk())
        .andExpect(view().name("doctor-schedule"))
        .andExpect(model().attributeExists("doctor"))
        .andExpect(model().attributeExists("schedule"))
        .andExpect(model().attributeExists("selectedMonth"))
        .andExpect(model().attribute("doctor", testDoctor))
        .andExpect(model().attribute("selectedMonth", startDate));

    verify(doctorService).getDoctorById(1);
    verify(scheduleService).findByDoctorAndDateRange(1, startDate, endDate);
  }

  @Test
  public void viewSchedule_WhenDoctorNotFound_ShouldThrowNotFound() throws Exception {
    when(doctorService.getDoctorById(1)).thenReturn(null);

    mockMvc.perform(get("/doctors/1/schedule"))
        .andExpect(status().isNotFound());

    verify(doctorService).getDoctorById(1);
    verifyNoInteractions(scheduleService);
  }

  @Test
  public void generateDoctorSchedule_WithCurrentMonth_ShouldRedirect() throws Exception {
    LocalDate currentDate = LocalDate.now();
    LocalDate startDate = currentDate.withDayOfMonth(1);

    doNothing().when(doctorService).generateInitialScheduleForDoctor(1, startDate);

    mockMvc.perform(post("/doctors/1/generate-schedule"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/doctors/1/doctor-schedule"));

    verify(doctorService).generateInitialScheduleForDoctor(1, startDate);
  }
}