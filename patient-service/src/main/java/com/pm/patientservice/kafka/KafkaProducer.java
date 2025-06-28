package com.pm.patientservice.kafka;

import com.pm.patientservice.dto.PatientEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(PatientEventDTO eventDTO) {
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(eventDTO.getPatientId())
                .setName(eventDTO.getName())
                .setEmail(eventDTO.getEmail())
                .setEventType(eventDTO.getEventType())
                .build();

        try {
            kafkaTemplate.send("patient", event.toByteArray());

        } catch (Exception e) {
            log.error("Error sending event to Kafka: {}", e.getMessage());
        }

    }
}
