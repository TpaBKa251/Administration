package ru.tpu.hostel.administration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.administration.client.UserServiceClient;
import ru.tpu.hostel.administration.dto.request.DocumentEditRequestDto;
import ru.tpu.hostel.administration.dto.request.DocumentRequestDto;
import ru.tpu.hostel.administration.dto.response.DocumentResponseDto;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.enums.DocumentType;
import ru.tpu.hostel.administration.exception.DocumentNotFound;
import ru.tpu.hostel.administration.exception.UserNotFound;
import ru.tpu.hostel.administration.mapper.DocumentMapper;
import ru.tpu.hostel.administration.repository.DocumentRepository;
import ru.tpu.hostel.administration.service.DocumentService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public DocumentResponseDto addDocument(DocumentRequestDto documentRequestDto) {
        ResponseEntity<?> response = userServiceClient.getUserById(documentRequestDto.user());

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new UserNotFound("Пользователь не найден");
        }

        Document document = DocumentMapper.mapDocumentRequestToDocument(documentRequestDto);

        documentRepository.save(document);

        return DocumentMapper.mapDocumentToDocumentResponseDto(document);
    }

    @Override
    public DocumentResponseDto editDocument(DocumentEditRequestDto documentEditRequestDto) {
        Document document = documentRepository.findById(documentEditRequestDto.id())
                .orElseThrow(() -> new DocumentNotFound("Документ не найден"));

        document.setStartDate(documentEditRequestDto.startDate());
        document.setEndDate(documentEditRequestDto.endDate());

        documentRepository.save(document);

        return DocumentMapper.mapDocumentToDocumentResponseDto(document);
    }

    @Override
    public DocumentResponseDto getDocumentById(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFound("Документ не найден"));

        return DocumentMapper.mapDocumentToDocumentResponseDto(document);
    }

    @Override
    public List<DocumentResponseDto> getAllUserDocuments(UUID userId) {
        List<Document> documents = documentRepository.findAllByUser(userId);

        if (documents.isEmpty()) {
            throw new DocumentNotFound("Документ не найден");
        }

        return documents
                .stream()
                .map(DocumentMapper::mapDocumentToDocumentResponseDto)
                .toList();
    }

    @Override
    public DocumentResponseDto getUserDocumentsByType(UUID userId, DocumentType documentType) {
        Document document = documentRepository.findByUserAndType(userId, documentType)
                .orElseThrow(() -> new DocumentNotFound("Документ не найден"));

        return DocumentMapper.mapDocumentToDocumentResponseDto(document);
    }

    @Override
    public List<DocumentResponseDto> getAllUserDocuments() {
        List<Document> documents = documentRepository.findAll();

        if (documents.isEmpty()) {
            throw new DocumentNotFound("Документ не найден");
        }

        return documents.stream()
                .map(DocumentMapper::mapDocumentToDocumentResponseDto)
                .toList();
    }
}
