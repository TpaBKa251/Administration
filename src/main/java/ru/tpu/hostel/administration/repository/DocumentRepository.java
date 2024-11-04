package ru.tpu.hostel.administration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.enums.DocumentType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findAllByType(DocumentType type);

    List<Document> findAllByUser(UUID userId);

    Optional<Document> findByUserAndType(UUID userId, DocumentType type);
}
