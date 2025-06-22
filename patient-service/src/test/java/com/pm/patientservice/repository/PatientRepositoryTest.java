package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldReturnTrueWhenEmailExists() {
        // Given
        Patient patient = createPatient();
        entityManager.persistAndFlush(patient);

        // When
        boolean exists = patientRepository.existsByEmail(patient.getEmail());

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // When
        boolean exists = patientRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldReturnTrueWhenEmailExistsForDifferentPatient() {
        // Given
        Patient patient1 = createPatient();
        Patient patient2 = createPatient();
        patient2.setEmail("different@example.com");
        
        entityManager.persistAndFlush(patient1);
        entityManager.persistAndFlush(patient2);

        // When
        boolean exists = patientRepository.existsByEmailAndIdNot(patient1.getEmail(), patient2.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailExistsForSamePatient() {
        // Given
        Patient patient = createPatient();
        entityManager.persistAndFlush(patient);

        // When
        boolean exists = patientRepository.existsByEmailAndIdNot(patient.getEmail(), patient.getId());

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldSaveAndFindPatient() {
        // Given
        Patient patient = createPatient();

        // When
        Patient saved = patientRepository.save(patient);
        Patient found = patientRepository.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals(patient.getName(), found.getName());
        assertEquals(patient.getEmail(), found.getEmail());
    }

    private Patient createPatient() {
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEmail("john@example.com");
        patient.setAddress("123 Main St");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setRegisteredDate(LocalDate.of(2024, 1, 1));
        return patient;
    }
}