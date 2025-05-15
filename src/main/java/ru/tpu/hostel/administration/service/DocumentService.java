package ru.tpu.hostel.administration.service;

import ru.tpu.hostel.administration.dto.request.DocumentEditRequestDto;
import ru.tpu.hostel.administration.dto.request.DocumentRequestDto;
import ru.tpu.hostel.administration.dto.response.DocumentResponseDto;
import ru.tpu.hostel.administration.entity.DocumentType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DocumentService {

    DocumentResponseDto addDocument(DocumentRequestDto documentRequestDto);

    DocumentResponseDto editDocument(DocumentEditRequestDto documentEditRequestDto);

    DocumentResponseDto getDocumentById(UUID documentId);

    List<DocumentResponseDto> getAllUserDocuments(UUID userId);

    DocumentResponseDto getUserDocumentsByType(UUID userId, DocumentType documentType);

    List<DocumentResponseDto> getAllUserDocuments(
            int page,
            int size,
            Boolean flura,
            LocalDate fluraTime,
            Boolean cert,
            LocalDate certDate
    );

    List<DocumentResponseDto> getAllDocumentsByUsers(List<UUID> userIds);
}
