package ru.tpu.hostel.administration.mapper;

import org.junit.jupiter.api.Test;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.dto.request.DocumentRequestDto;
import ru.tpu.hostel.administration.dto.response.DocumentResponseDto;
import ru.tpu.hostel.administration.dto.response.DocumentShortResponseDto;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.entity.DocumentType;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentMapperTest {

    @Test
    void mapDocumentRequestToDocumentWithSuccess() {
        DocumentRequestDto dto = TestData.defaultDocumentRequestDto();

        Document result = DocumentMapper.mapDocumentRequestToDocument(dto);

        assertThat(result.getUser()).isEqualTo(TestData.USER_ID);
        assertThat(result.getType()).isEqualTo(DocumentType.FLUOROGRAPHY);
        assertThat(result.getStartDate()).isEqualTo(TestData.START_DATE);
        assertThat(result.getEndDate()).isEqualTo(TestData.END_DATE);
    }

    @Test
    void mapDocumentToDocumentResponseDtoWithSuccess() {
        Document document = TestData.defaultDocument();

        DocumentResponseDto result = DocumentMapper.mapDocumentToDocumentResponseDto(document);

        assertThat(result.id()).isEqualTo(TestData.DOCUMENT_ID);
        assertThat(result.user()).isEqualTo(TestData.USER_ID);
        assertThat(result.type()).isEqualTo(DocumentType.FLUOROGRAPHY);
        assertThat(result.startDate()).isEqualTo(TestData.START_DATE);
        assertThat(result.endDate()).isEqualTo(TestData.END_DATE);
    }

    @Test
    void mapDocumentToDocumentShortResponseDtoWithSuccess() {
        Document document = TestData.defaultDocument();

        DocumentShortResponseDto result = DocumentMapper.mapDocumentToDocumentShortResponseDto(document);

        assertThat(result.type()).isEqualTo(DocumentType.FLUOROGRAPHY);
        assertThat(result.startDate()).isEqualTo(TestData.START_DATE);
        assertThat(result.endDate()).isEqualTo(TestData.END_DATE);
    }
}
