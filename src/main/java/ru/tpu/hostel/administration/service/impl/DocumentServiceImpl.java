package ru.tpu.hostel.administration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.administration.dto.request.DocumentEditRequestDto;
import ru.tpu.hostel.administration.dto.request.DocumentRequestDto;
import ru.tpu.hostel.administration.dto.response.DocumentResponseDto;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.entity.DocumentType;
import ru.tpu.hostel.administration.mapper.DocumentMapper;
import ru.tpu.hostel.administration.repository.DocumentRepository;
import ru.tpu.hostel.administration.service.DocumentService;
import ru.tpu.hostel.internal.exception.ServiceException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    public static final String DOCUMENT_NOT_FOUND = "Документ не найден";

    private final DocumentRepository documentRepository;

    @Transactional
    @Override
    public DocumentResponseDto addDocument(DocumentRequestDto documentRequestDto) {
        Document document = DocumentMapper.mapDocumentRequestToDocument(documentRequestDto);
        documentRepository.save(document);
        return DocumentMapper.mapDocumentToDocumentResponseDto(document);
    }

    @Transactional
    @Override
    public DocumentResponseDto editDocument(DocumentEditRequestDto documentEditRequestDto) {
        Document document = documentRepository.findByIdOptimistic(documentEditRequestDto.id())
                .orElseThrow(() -> new ServiceException.BadRequest(DOCUMENT_NOT_FOUND));

        document.setStartDate(documentEditRequestDto.startDate());
        document.setEndDate(documentEditRequestDto.endDate());

        try {
            documentRepository.save(document);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ServiceException.Conflict("Не удалось отредактировать документ. Попробуйте позже");
        }

        return DocumentMapper.mapDocumentToDocumentResponseDto(document);
    }

    @Override
    public DocumentResponseDto getDocumentById(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ServiceException.BadRequest(DOCUMENT_NOT_FOUND));

        return DocumentMapper.mapDocumentToDocumentResponseDto(document);
    }

    @Override
    public List<DocumentResponseDto> getAllUserDocuments(UUID userId) {
        List<Document> documents = documentRepository.findAllByUser(userId);

        if (documents.isEmpty()) {
            throw new ServiceException.BadRequest(DOCUMENT_NOT_FOUND);
        }

        return documents
                .stream()
                .map(DocumentMapper::mapDocumentToDocumentResponseDto)
                .toList();
    }

    @Override
    public DocumentResponseDto getUserDocumentsByType(UUID userId, DocumentType documentType) {
        Document document = documentRepository.findByUserAndType(userId, documentType)
                .orElseThrow(() -> new ServiceException.BadRequest(DOCUMENT_NOT_FOUND));

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
