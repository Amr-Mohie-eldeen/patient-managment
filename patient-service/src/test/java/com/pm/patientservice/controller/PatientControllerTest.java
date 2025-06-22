package com.pm.patientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@SuppressWarnings("deprecation")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllPatients() throws Exception {
        // Given
        PatientResponseDTO patient = createPatientResponse();
        when(patientService.getPatients()).thenReturn(List.of(patient));

        // When & Then
        mockMvc.perform(get("/patients"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("John Doe"))
               .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    void shouldCreatePatientSuccessfully() throws Exception {
        // Given
        PatientRequestDTO request = createPatientRequest();
        PatientResponseDTO response = createPatientResponse();
        when(patientService.createPatient(any(PatientRequestDTO.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void shouldReturnBadRequestForInvalidPatient() throws Exception {
        // Given
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName(""); // Invalid name

        // When & Then
        mockMvc.perform(post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEmailExists() throws Exception {
        // Given
        PatientRequestDTO request = createPatientRequest();
        when(patientService.createPatient(any(PatientRequestDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("Email exists"));

        // When & Then
        mockMvc.perform(post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Email address already exists"));
    }

    @Test
    void shouldUpdatePatientSuccessfully() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        PatientRequestDTO request = createPatientRequest();
        PatientResponseDTO response = createPatientResponse();
        when(patientService.updatePatient(any(UUID.class),
                                          any(PatientRequestDTO.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/patients/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void shouldReturnBadRequestWhenPatientNotFound() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        PatientRequestDTO request = createPatientRequest();
        when(patientService.updatePatient(any(UUID.class), any(PatientRequestDTO.class)))
                .thenThrow(new PatientNotFoundException("Patient not found"));

        // When & Then
        mockMvc.perform(put("/patients/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    void shouldDeletePatientSuccessfully() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/patients/{id}", id))
               .andExpect(status().isNoContent());
    }

    private PatientRequestDTO createPatientRequest() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAddress("123 Main St");
        request.setDateOfBirth("1990-01-01");
        request.setRegisteredDate("2024-01-01");
        return request;
    }

    private PatientResponseDTO createPatientResponse() {
        PatientResponseDTO response = new PatientResponseDTO();
        response.setId(UUID.randomUUID().toString());
        response.setName("John Doe");
        response.setEmail("john@example.com");
        response.setAddress("123 Main St");
        response.setDateOfBirth("1990-01-01");
        return response;
    }
}