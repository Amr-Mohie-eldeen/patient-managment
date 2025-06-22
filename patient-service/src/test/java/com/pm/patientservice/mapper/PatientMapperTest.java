package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PatientMapperTest {

    @Test
    void shouldMapPatientToDTO() {
        // Given
        Patient patient = createPatient();

        // When
        PatientResponseDTO dto = PatientMapper.toDTO(patient);

        // Then
        assertEquals(patient.getId().toString(), dto.getId());
        assertEquals(patient.getName(), dto.getName());
        assertEquals(patient.getEmail(), dto.getEmail());
        assertEquals(patient.getAddress(), dto.getAddress());
        assertEquals(patient.getDateOfBirth().toString(), dto.getDateOfBirth());
    }

    @Test
    void shouldMapRequestDTOToPatient() {
        // Given
        PatientRequestDTO dto = createPatientRequestDTO();

        // When
        Patient patient = PatientMapper.toModel(dto);

        // Then
        assertEquals(dto.getName(), patient.getName());
        assertEquals(dto.getEmail(), patient.getEmail());
        assertEquals(dto.getAddress(), patient.getAddress());
        assertEquals(LocalDate.parse(dto.getDateOfBirth()), patient.getDateOfBirth());
        assertEquals(LocalDate.parse(dto.getRegisteredDate()), patient.getRegisteredDate());
    }

    private Patient createPatient() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setName("John Doe");
        patient.setEmail("john@example.com");
        patient.setAddress("123 Main St");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setRegisteredDate(LocalDate.of(2024, 1, 1));
        return patient;
    }

    private PatientRequestDTO createPatientRequestDTO() {
        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setAddress("123 Main St");
        dto.setDateOfBirth("1990-01-01");
        dto.setRegisteredDate("2024-01-01");
        return dto;
    }
}