package com.legal.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.legal.entites.DocumentProcedure;

@Repository
public interface DocumentProcedureRepository extends JpaRepository<DocumentProcedure, Long> {

    // One row per state + document type (not multiple steps anymore)
    Optional<DocumentProcedure> findByStateNameAndDocumentType(
            String stateName, String documentType);

    boolean existsByStateNameAndDocumentType(String stateName, String documentType);

    java.util.List<DocumentProcedure> findByStateName(String stateName);
}
