package com.legal.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.legal.entites.DocumentIssue;

@Repository
public interface DocumentIssueRepository extends JpaRepository<DocumentIssue, Long> {

    Optional<DocumentIssue> findByStateNameAndDocumentType(
            String stateName, String documentType);

    boolean existsByStateNameAndDocumentType(String stateName, String documentType);
}
