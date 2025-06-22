package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private BillingServiceGrpcClient billingServiceGrpcClient;
    @Mock
    private KafkaProducer kafkaProducer;

    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientService = new PatientService(patientRepository, billingServiceGrpcClient,
                                            kafkaProducer);
    }

    @Test
    void shouldCreatePatientSuccessfully() {
        // Given
        PatientRequestDTO request = createTestPatientRequest();
        Patient savedPatient = createTestPatient();

        when(patientRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        // When
        PatientResponseDTO result = patientService.createPatient(request);

        // Then
        assertNotNull(result);
        assertEquals(savedPatient.getName(), result.getName());
        assertEquals(savedPatient.getEmail(), result.getEmail());
        verify(billingServiceGrpcClient).createBillingAccount(any(), any(), any());
        verify(kafkaProducer).sendEvent(any(Patient.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        PatientRequestDTO request = createTestPatientRequest();
        when(patientRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class,
                     () -> patientService.createPatient(request));
        verify(patientRepository, never()).save(any());
        verify(billingServiceGrpcClient, never()).createBillingAccount(any(), any(), any());
    }

    @Test
    void shouldGetAllPatients() {
        // Given
        List<Patient> patients = List.of(createTestPatient());
        when(patientRepository.findAll()).thenReturn(patients);

        // When
        List<PatientResponseDTO> result = patientService.getPatients();

        // Then
        assertEquals(1, result.size());
        assertEquals(patients.get(0).getName(), result.get(0).getName());
    }

    @Test
    void shouldUpdatePatientSuccessfully() {
        // Given
        UUID id = UUID.randomUUID();
        PatientRequestDTO request = createTestPatientRequest();
        Patient existingPatient = createTestPatient();
        existingPatient.setId(id);

        when(patientRepository.findById(id)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.existsByEmailAndIdNot(request.getEmail(), id)).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(existingPatient);

        // When
        PatientResponseDTO result = patientService.updatePatient(id, request);

        // Then
        assertNotNull(result);
        verify(patientRepository).save(existingPatient);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentPatient() {
        // Given
        UUID id = UUID.randomUUID();
        PatientRequestDTO request = createTestPatientRequest();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PatientNotFoundException.class,
                     () -> patientService.updatePatient(id, request));
    }

    @Test
    void shouldDeletePatientSuccessfully() {
        // Given
        UUID id = UUID.randomUUID();
        Patient patient = createTestPatient();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        // When
        patientService.deletePatient(id);

        // Then
        verify(patientRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentPatient() {
        // Given
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PatientNotFoundException.class, () -> patientService.deletePatient(id));
        verify(patientRepository, never()).deleteById(any());
    }

    private PatientRequestDTO createTestPatientRequest() {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAddress("123 Main St");
        request.setDateOfBirth("1990-01-01");
        request.setRegisteredDate("2024-01-01");
        return request;
    }

    private Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setName("John Doe");
        patient.setEmail("john@example.com");
        patient.setAddress("123 Main St");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setRegisteredDate(LocalDate.of(2024, 1, 1));
        return patient;
    }
}