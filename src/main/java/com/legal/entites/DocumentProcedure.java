package com.legal.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "document_procedure")
public class DocumentProcedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stateName; // e.g. "Maharashtra"
    private String documentType; // e.g. "ration_card"

    @Column(columnDefinition = "TEXT")
    private String introduction; // Intro paragraph about the document

    @Column(columnDefinition = "TEXT")
    private String quickFacts; // Important points (HTML list/alert)

    @Column(columnDefinition = "TEXT")
    private String documentsRequired; // Required documents section (HTML)

    @Column(columnDefinition = "TEXT")
    private String onlineApplication; // Step-by-step online process (HTML)

    @Column(columnDefinition = "TEXT")
    private String offlineApplication; // Step-by-step offline process (HTML)

    @Column(columnDefinition = "TEXT")
    private String afterApplying; // What to do after applying (HTML)

    @Column(columnDefinition = "TEXT")
    private String helpfulLinks; // Useful links section (HTML)

    // ── Getters & Setters ──────────────────────────────────────────────────────

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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getQuickFacts() {
        return quickFacts;
    }

    public void setQuickFacts(String quickFacts) {
        this.quickFacts = quickFacts;
    }

    public String getDocumentsRequired() {
        return documentsRequired;
    }

    public void setDocumentsRequired(String documentsRequired) {
        this.documentsRequired = documentsRequired;
    }

    public String getOnlineApplication() {
        return onlineApplication;
    }

    public void setOnlineApplication(String onlineApplication) {
        this.onlineApplication = onlineApplication;
    }

    public String getOfflineApplication() {
        return offlineApplication;
    }

    public void setOfflineApplication(String offlineApplication) {
        this.offlineApplication = offlineApplication;
    }

    public String getAfterApplying() {
        return afterApplying;
    }

    public void setAfterApplying(String afterApplying) {
        this.afterApplying = afterApplying;
    }

    public String getHelpfulLinks() {
        return helpfulLinks;
    }

    public void setHelpfulLinks(String helpfulLinks) {
        this.helpfulLinks = helpfulLinks;
    }
}
