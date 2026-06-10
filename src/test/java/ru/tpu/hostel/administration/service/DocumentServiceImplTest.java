package ru.tpu.hostel.administration.service;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.dto.response.DocumentResponseDto;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.entity.DocumentType;
import ru.tpu.hostel.administration.repository.DocumentRepository;
import ru.tpu.hostel.administration.service.impl.DocumentServiceImpl;
import ru.tpu.hostel.internal.exception.ServiceException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    void addDocumentWithSuccess() {
        when(documentRepository.save(any(Document.class))).thenReturn(TestData.defaultDocument());

        DocumentResponseDto result = documentService.addDocument(TestData.defaultDocumentRequestDto());

        assertThat(result.id()).isEqualTo(TestData.DOCUMENT_ID);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void addDocumentWhenConflict() {
        when(documentRepository.save(any(Document.class)))
                .thenThrow(new ConstraintViolationException("duplicate", null, "document"));

        assertThatThrownBy(() -> documentService.addDocument(TestData.defaultDocumentRequestDto()))
                .isInstanceOf(ServiceException.Conflict.class);
    }

    @Test
    void editDocumentWithSuccess() {
        Document document = TestData.defaultDocument();
        when(documentRepository.findByIdOptimistic(TestData.DOCUMENT_ID)).thenReturn(Optional.of(document));

        DocumentResponseDto result = documentService.editDocument(TestData.documentEditRequestDto(TestData.DOCUMENT_ID));

        assertThat(result.endDate()).isEqualTo(TestData.FUTURE_END_DATE);
        verify(documentRepository).save(document);
    }

    @Test
    void editDocumentWhenNotFound() {
        when(documentRepository.findByIdOptimistic(TestData.DOCUMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.editDocument(TestData.documentEditRequestDto(TestData.DOCUMENT_ID)))
                .isInstanceOf(ServiceException.BadRequest.class);
    }

    @Test
    void editDocumentWhenOptimisticLock() {
        Document document = TestData.defaultDocument();
        when(documentRepository.findByIdOptimistic(TestData.DOCUMENT_ID)).thenReturn(Optional.of(document));
        doThrow(new ObjectOptimisticLockingFailureException(Document.class, TestData.DOCUMENT_ID))
                .when(documentRepository).save(document);

        assertThatThrownBy(() -> documentService.editDocument(TestData.documentEditRequestDto(TestData.DOCUMENT_ID)))
                .isInstanceOf(ServiceException.Conflict.class);
    }

    @Test
    void getDocumentByIdWithSuccess() {
        when(documentRepository.findById(TestData.DOCUMENT_ID)).thenReturn(Optional.of(TestData.defaultDocument()));

        DocumentResponseDto result = documentService.getDocumentById(TestData.DOCUMENT_ID);

        assertThat(result.id()).isEqualTo(TestData.DOCUMENT_ID);
    }

    @Test
    void getDocumentByIdWhenNotFound() {
        when(documentRepository.findById(TestData.DOCUMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getDocumentById(TestData.DOCUMENT_ID))
                .isInstanceOf(ServiceException.BadRequest.class);
    }

    @Test
    void getAllUserDocumentsWithSuccess() {
        when(documentRepository.findAllByUser(TestData.USER_ID))
                .thenReturn(List.of(TestData.defaultDocument()));

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(TestData.USER_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUserDocumentsWhenEmpty() {
        when(documentRepository.findAllByUser(TestData.USER_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> documentService.getAllUserDocuments(TestData.USER_ID))
                .isInstanceOf(ServiceException.BadRequest.class);
    }

    @Test
    void getUserDocumentsByTypeWithSuccess() {
        when(documentRepository.findByUserAndType(TestData.USER_ID, DocumentType.FLUOROGRAPHY))
                .thenReturn(Optional.of(TestData.defaultDocument()));

        DocumentResponseDto result = documentService.getUserDocumentsByType(TestData.USER_ID, DocumentType.FLUOROGRAPHY);

        assertThat(result.type()).isEqualTo(DocumentType.FLUOROGRAPHY);
    }

    @Test
    void getUserDocumentsByTypeWhenNotFound() {
        when(documentRepository.findByUserAndType(TestData.USER_ID, DocumentType.FLUOROGRAPHY))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getUserDocumentsByType(TestData.USER_ID, DocumentType.FLUOROGRAPHY))
                .isInstanceOf(ServiceException.BadRequest.class);
    }

    @Test
    void getAllUserDocumentsWhenBothPastAllPast() {
        Page<Document> page = new PageImpl<>(List.of(TestData.defaultDocument()));
        when(documentRepository.findAllByEndDateLessThanEqual(any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page);

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(
                TestData.PAGE, TestData.SIZE, true, TestData.END_DATE, true, TestData.END_DATE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUserDocumentsWhenBothNotPastAllAfter() {
        Page<Document> page = new PageImpl<>(List.of(TestData.defaultDocument()));
        when(documentRepository.findAllByEndDateAfter(any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page);

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(
                TestData.PAGE, TestData.SIZE, false, TestData.END_DATE, false, TestData.END_DATE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUserDocumentsWhenBothFluraPastForOne() {
        Page<Document> page = new PageImpl<>(List.of(TestData.defaultDocument()));
        when(documentRepository.findAllByEndDateLessThanEqualAndTypeForOne(
                any(LocalDate.class), eq(DocumentType.FLUOROGRAPHY), any(Pageable.class)))
                .thenReturn(page);

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(
                TestData.PAGE, TestData.SIZE, true, TestData.END_DATE, false, TestData.END_DATE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUserDocumentsWhenBothCertPastForOne() {
        Page<Document> page = new PageImpl<>(List.of(TestData.defaultDocument()));
        when(documentRepository.findAllByEndDateLessThanEqualAndTypeForOne(
                any(LocalDate.class), eq(DocumentType.CERTIFICATE), any(Pageable.class)))
                .thenReturn(page);

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(
                TestData.PAGE, TestData.SIZE, false, TestData.END_DATE, true, TestData.END_DATE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUserDocumentsWhenFluraPast() {
        Page<Document> page = new PageImpl<>(List.of(TestData.defaultDocument()));
        when(documentRepository.findAllByEndDateLessThanEqualAndType(
                any(LocalDate.class), eq(DocumentType.FLUOROGRAPHY), any(Pageable.class)))
                .thenReturn(page);

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(
                TestData.PAGE, TestData.SIZE, true, TestData.END_DATE, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUserDocumentsWhenFluraNotPast() {
        Page<Document> page = new PageImpl<>(List.of(TestData.defaultDocument()));
        when(documentRepository.findAllByEndDateAfterAndType(
                any(LocalDate.class), eq(DocumentType.FLUOROGRAPHY), any(Pageable.class)))
                .thenReturn(page);

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(
                TestData.PAGE, TestData.SIZE, false, TestData.END_DATE, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUserDocumentsWhenCertPast() {
        Page<Document> page = new PageImpl<>(List.of(TestData.defaultDocument()));
        when(documentRepository.findAllByEndDateLessThanEqualAndType(
                any(LocalDate.class), eq(DocumentType.CERTIFICATE), any(Pageable.class)))
                .thenReturn(page);

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(
                TestData.PAGE, TestData.SIZE, null, null, true, TestData.END_DATE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUserDocumentsWhenCertNotPast() {
        Page<Document> page = new PageImpl<>(List.of(TestData.defaultDocument()));
        when(documentRepository.findAllByEndDateAfterAndType(
                any(LocalDate.class), eq(DocumentType.CERTIFICATE), any(Pageable.class)))
                .thenReturn(page);

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(
                TestData.PAGE, TestData.SIZE, null, null, false, TestData.END_DATE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUserDocumentsWhenNoFilter() {
        Page<Document> page = new PageImpl<>(List.of(TestData.defaultDocument()));
        when(documentRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<DocumentResponseDto> result = documentService.getAllUserDocuments(
                TestData.PAGE, TestData.SIZE, null, null, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllDocumentsByUsersWithSuccess() {
        when(documentRepository.findByUserInOrderByUserAscTypeAsc(anyList()))
                .thenReturn(List.of(TestData.defaultDocument()));

        List<DocumentResponseDto> result = documentService.getAllDocumentsByUsers(List.of(TestData.USER_ID));

        assertThat(result).hasSize(1);
    }
}
