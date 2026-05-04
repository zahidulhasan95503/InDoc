package com.legal.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.legal.dao.DocumentProcedureRepository;
import com.legal.dao.DocumentIssueRepository;
import com.legal.entites.DocumentProcedure;
import com.legal.entites.DocumentIssue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import java.io.IOException;

/**
 * Seeds the document_procedure and document_issue tables on startup using JSON files.
 * This "Smart Loader" system ensures that all document data is externalized for maintainability.
 * Safe in production — updates existing records or seeds new ones.
 */
@Component
public class DataInitializer implements ApplicationRunner {

        @Autowired
        private DocumentProcedureRepository procedureRepository;

        @Autowired
        private DocumentIssueRepository issueRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @Override
        public void run(ApplicationArguments args) throws Exception {
                // 1. Seed from JSON files (The "Smart" way)
                loadProceduresFromJson();
                loadIssuesFromJson();
        }

        private void loadProceduresFromJson() {
                try {
                        Resource[] resources = new PathMatchingResourcePatternResolver()
                                        .getResources("classpath:seeds/procedures/*.json");
                        for (Resource resource : resources) {
                                DocumentProcedure doc = objectMapper.readValue(resource.getInputStream(),
                                                DocumentProcedure.class);

                                // Check if it already exists
                                procedureRepository
                                                .findByStateNameAndDocumentType(doc.getStateName(),
                                                                doc.getDocumentType())
                                                .ifPresentOrElse(
                                                                existing -> {
                                                                        // Update existing record
                                                                        existing.setIntroduction(doc.getIntroduction());
                                                                        existing.setQuickFacts(doc.getQuickFacts());
                                                                        existing.setDocumentsRequired(
                                                                                        doc.getDocumentsRequired());
                                                                        existing.setOnlineApplication(
                                                                                        doc.getOnlineApplication());
                                                                        existing.setOfflineApplication(
                                                                                        doc.getOfflineApplication());
                                                                        existing.setAfterApplying(
                                                                                        doc.getAfterApplying());
                                                                        existing.setHelpfulLinks(doc.getHelpfulLinks());
                                                                        procedureRepository.save(existing);
                                                                        System.out.println("Updated Procedure JSON: "
                                                                                        + doc.getDocumentType() + " - "
                                                                                        + doc.getStateName());
                                                                },
                                                                () -> {
                                                                        // Save new record
                                                                        procedureRepository.save(doc);
                                                                        System.out.println("Seeded New Procedure JSON: "
                                                                                        + doc.getDocumentType() + " - "
                                                                                        + doc.getStateName());
                                                                });
                        }
                } catch (IOException e) {
                        System.err.println("Error loading procedure JSONs: " + e.getMessage());
                }
        }

        private void loadIssuesFromJson() {
                try {
                        Resource[] resources = new PathMatchingResourcePatternResolver()
                                        .getResources("classpath:seeds/issues/*.json");
                        for (Resource resource : resources) {
                                DocumentIssue issue = objectMapper.readValue(resource.getInputStream(),
                                                DocumentIssue.class);

                                // Check if it already exists
                                issueRepository.findByStateNameAndDocumentType(issue.getStateName(),
                                                issue.getDocumentType())
                                                .ifPresentOrElse(
                                                                existing -> {
                                                                        // Update existing record
                                                                        existing.setCommonIssues(
                                                                                        issue.getCommonIssues());
                                                                        issueRepository.save(existing);
                                                                        System.out.println("Updated Issue JSON: "
                                                                                        + issue.getDocumentType()
                                                                                        + " - " + issue.getStateName());
                                                                },
                                                                () -> {
                                                                        // Save new record
                                                                        issueRepository.save(issue);
                                                                        System.out.println("Seeded New Issue JSON: "
                                                                                        + issue.getDocumentType()
                                                                                        + " - " + issue.getStateName());
                                                                });
                        }
                } catch (IOException e) {
                        System.err.println("Error loading issue JSONs: " + e.getMessage());
                }
        }
}
