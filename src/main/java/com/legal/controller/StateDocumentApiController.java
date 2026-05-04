package com.legal.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.legal.dao.DocumentCentreRepository;
import com.legal.dao.DocumentIssueRepository;
import com.legal.dao.DocumentProcedureRepository;
import com.legal.entites.DocumentCentre;
import com.legal.entites.DocumentIssue;
import com.legal.entites.DocumentProcedure;

@RestController
@RequestMapping("/api")
public class StateDocumentApiController {

        @Autowired
        private DocumentProcedureRepository procedureRepository;

        @Autowired
        private DocumentIssueRepository issueRepository;

        @Autowired
        private DocumentCentreRepository centreRepository;

        // ── All Indian states list (used to populate dropdown on all 3 pages) ────
        @GetMapping("/states")
        public List<String> getAllStates() {
                return Arrays.asList(
                                "Andhra Pradesh", "Karnataka", "Madhya Pradesh", "Maharashtra",
                                "Rajasthan", "Tamil Nadu", "Telangana", "Uttar Pradesh", "Delhi", "West bengal");
        }

        // ── Full document content for a state + document type ────────────────────
        // Called by document.html
        // Example: GET /api/procedures?state=Maharashtra&type=ration_card
        @GetMapping("/procedures")
        public ResponseEntity<DocumentProcedure> getProcedures(
                        @RequestParam String state,
                        @RequestParam String type) {
                return procedureRepository.findByStateNameAndDocumentType(state, type)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        // ── Common issues for a document + state ─────────────────────────────────
        // Called by document_issue.html
        // Example: GET /api/issues?state=Maharashtra&type=ration_card
        @GetMapping("/issues")
        public List<DocumentIssue> getIssues(
                        @RequestParam String state,
                        @RequestParam String type) {
                return issueRepository.findByStateNameAndDocumentType(state, type)
                                .map(List::of)
                                .orElse(List.of());
        }

        // ── Centre info for a document + state ───────────────────────────────────
        // Called by document_centre.html
        // Example: GET /api/centre?state=Maharashtra&type=ration_card
        @GetMapping("/centre")
        public ResponseEntity<DocumentCentre> getCentre(
                        @RequestParam String state,
                        @RequestParam String type) {
                return centreRepository.findByStateNameAndDocumentType(state, type)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }
}
