package com.pm.patientservice.dto;

public class PatientEventDTO {
    private String patientId;
    private String name;
    private String email;
    private String eventType;

    public PatientEventDTO(String patientId, String name, String email, String eventType) {
        this.patientId = patientId;
        this.name = name;
        this.email = email;
        this.eventType = eventType;
    }

    public String getPatientId() { return patientId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getEventType() { return eventType; }
}