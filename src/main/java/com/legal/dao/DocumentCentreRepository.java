package com.legal.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.legal.entites.DocumentCentre;

@Repository
public interface DocumentCentreRepository extends JpaRepository<DocumentCentre, Long> {

    Optional<DocumentCentre> findByStateNameAndDocumentType(
            String stateName, String documentType);
}
