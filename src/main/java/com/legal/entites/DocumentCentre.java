package com.legal.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "document_centre")
public class DocumentCentre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stateName; // e.g. "Maharashtra"
    private String documentType; // e.g. "ration_card"

    @Column(columnDefinition = "TEXT")
    private String centreDescription; // e.g. "Visit your nearest Taluka office..."

    private String mapsSearchKeyword; // Passed to Google Maps embed

    @Column(columnDefinition = "TEXT")
    private String contactInfo; // Helpline numbers, email, address

    private String workingHours; // e.g. "Mon-Sat, 10am-5pm"

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getCentreDescription() {
        return centreDescription;
    }

    public void setCentreDescription(String centreDescription) {
        this.centreDescription = centreDescription;
    }

    public String getMapsSearchKeyword() {
        return mapsSearchKeyword;
    }

    public void setMapsSearchKeyword(String mapsSearchKeyword) {
        this.mapsSearchKeyword = mapsSearchKeyword;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
}
