package ru.tpu.hostel.administration.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.entity.DocumentType;
import ru.tpu.hostel.administration.repository.util.RepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    private Document fluorography;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
        fluorography = documentRepository.save(TestData.newDocument(
                null, TestData.USER_ID, DocumentType.FLUOROGRAPHY, TestData.START_DATE, TestData.END_DATE));
        documentRepository.save(TestData.newDocument(
                null, TestData.USER_ID, DocumentType.CERTIFICATE, TestData.START_DATE, TestData.END_DATE));
    }

    @Test
    void findAllByTypeWithSuccess() {
        List<Document> result = documentRepository.findAllByType(DocumentType.FLUOROGRAPHY);

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByUserWithSuccess() {
        List<Document> result = documentRepository.findAllByUser(TestData.USER_ID);

        assertThat(result).hasSize(2);
    }

    @Test
    void findByUserAndTypeWhenExists() {
        Optional<Document> result = documentRepository.findByUserAndType(TestData.USER_ID, DocumentType.FLUOROGRAPHY);

        assertThat(result).isPresent();
    }

    @Test
    void findByUserInOrderByUserAscTypeAscWithSuccess() {
        List<Document> result = documentRepository.findByUserInOrderByUserAscTypeAsc(List.of(TestData.USER_ID));

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllByEndDateEqualsWithSuccess() {
        List<Document> result = documentRepository.findAllByEndDateEquals(TestData.END_DATE);

        assertThat(result).hasSize(2);
    }

    @Test
    void findByIdOptimisticWhenExists() {
        Optional<Document> result = documentRepository.findByIdOptimistic(fluorography.getId());

        assertThat(result).isPresent();
    }
}
