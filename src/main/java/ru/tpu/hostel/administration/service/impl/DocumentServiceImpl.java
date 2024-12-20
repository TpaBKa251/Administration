package ru.tpu.hostel.administration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.administration.client.UserServiceClient;
import ru.tpu.hostel.administration.dto.request.DocumentEditRequestDto;
import ru.tpu.hostel.administration.dto.request.DocumentRequestDto;
import ru.tpu.hostel.administration.dto.response.DocumentResponseDto;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.enums.DocumentType;
import ru.tpu.hostel.administration.exception.DocumentNotFound;
import ru.tpu.hostel.administration.mapper.DocumentMapper;
import ru.tpu.hostel.administration.repository.DocumentRepository;
import ru.tpu.hostel.administration.service.DocumentService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public DocumentResponseDto addDocument(DocumentRequestDto documentRequestDto) {
//        ResponseEntity<?> response = userServiceClient.getUserById(documentRequestDto.user());
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new UserNotFound("Пользователь не найден");
//        }

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
    public List<DocumentResponseDto> getAllUserDocuments(
            int page,
            int size,
            Boolean fluraPast,
            LocalDate fluraDate,
            Boolean certPast,
            LocalDate certDate
    ) {

        Sort sort = Sort.by(
                Sort.Order.asc("user"),
                Sort.Order.asc("type")
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> documents;

        if (fluraPast != null && fluraDate != null && certPast != null && certDate != null) {
            if (fluraPast && certPast) {
                documents = documentRepository.findAllByEndDateLessThanEqual(
                        fluraDate,
                        pageable
                );
            } else if (!fluraPast && !certPast) {
                documents = documentRepository.findAllByEndDateAfter(
                        fluraDate,
                        pageable
                );
            } else if (fluraPast) {
                documents = documentRepository.findAllByEndDateLessThanEqualAndTypeForOne(
                        fluraDate,
                        DocumentType.FLUOROGRAPHY,
                        pageable
                );
            } else {
                documents = documentRepository.findAllByEndDateLessThanEqualAndTypeForOne(
                        certDate,
                        DocumentType.CERTIFICATE,
                        pageable
                );
            }
        } else if (fluraPast != null && fluraDate != null) {
            if (fluraPast) {
                documents = documentRepository.findAllByEndDateLessThanEqualAndType(
                        fluraDate,
                        DocumentType.FLUOROGRAPHY,
                        pageable
                );
            } else {
                documents = documentRepository.findAllByEndDateAfterAndType(
                        fluraDate,
                        DocumentType.FLUOROGRAPHY,
                        pageable
                );
            }
        } else if (certPast != null && certDate != null) {
            if (certPast) {
                documents = documentRepository.findAllByEndDateLessThanEqualAndType(
                        certDate,
                        DocumentType.CERTIFICATE,
                        pageable
                );
            } else {
                documents = documentRepository.findAllByEndDateAfterAndType(
                        certDate,
                        DocumentType.CERTIFICATE,
                        pageable
                );
            }
        } else {
            documents = documentRepository.findAll(pageable);
        }

        if (documents.isEmpty()) {
            throw new DocumentNotFound("Документы не найдены");
        }

        return documents.stream()
                .map(DocumentMapper::mapDocumentToDocumentResponseDto)
                .toList();
    }

    @Override
    public List<DocumentResponseDto> getAllDocumentsByUsers(List<UUID> userIds) {
        return documentRepository.findByUserInOrderByUserAscTypeAsc(userIds)
                .stream()
                .map(DocumentMapper::mapDocumentToDocumentResponseDto)
                .toList();
    }
}
