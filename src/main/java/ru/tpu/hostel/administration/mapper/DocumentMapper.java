package ru.tpu.hostel.administration.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.administration.dto.request.DocumentRequestDto;
import ru.tpu.hostel.administration.dto.response.DocumentResponseDto;
import ru.tpu.hostel.administration.dto.response.DocumentShortResponseDto;
import ru.tpu.hostel.administration.entity.Document;

@Component
public class DocumentMapper {

    public static Document mapDocumentRequestToDocument(DocumentRequestDto documentRequestDto) {
        Document document = new Document();
        document.setUser(documentRequestDto.user());
        document.setType(documentRequestDto.type());
        document.setStartDate(documentRequestDto.startDate());
        document.setEndDate(documentRequestDto.endDate());

        return document;
    }

    public static DocumentResponseDto mapDocumentToDocumentResponseDto(Document document) {
        return new DocumentResponseDto(
                document.getId(),
                document.getUser(),
                document.getType(),
                document.getStartDate(),
                document.getEndDate()
        );
    }

    public static DocumentShortResponseDto mapDocumentToDocumentShortResponseDto(Document document) {
        return new DocumentShortResponseDto(
                document.getType(),
                document.getStartDate(),
                document.getEndDate()
        );
    }
}
