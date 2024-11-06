package ru.tpu.hostel.administration.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.administration.dto.request.DocumentEditRequestDto;
import ru.tpu.hostel.administration.dto.request.DocumentRequestDto;
import ru.tpu.hostel.administration.dto.response.DocumentResponseDto;
import ru.tpu.hostel.administration.enums.DocumentType;
import ru.tpu.hostel.administration.service.DocumentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public DocumentResponseDto addDocument(@RequestBody @Valid DocumentRequestDto documentRequestDto) {
        return documentService.addDocument(documentRequestDto);
    }

    @PatchMapping("/edit")
    public DocumentResponseDto editDocument(@RequestBody @Valid DocumentEditRequestDto documentEditRequestDto) {
        return documentService.editDocument(documentEditRequestDto);
    }

    @GetMapping("/get_by_id")
    public DocumentResponseDto getDocumentById(UUID id) {
        return documentService.getDocumentById(id);
    }

    @GetMapping("/get/all/user")
    public List<DocumentResponseDto> getAllDocumentsByUser(UUID userId) {
        return documentService.getAllUserDocuments(userId);
    }

    @GetMapping("/get/all")
    public List<DocumentResponseDto> getAllDocuments() {

    }

    @GetMapping("/get/type/user")
    public DocumentResponseDto getDocumentByType(UUID userId, DocumentType documentType) {
        return documentService.getUserDocumentsByType(userId, documentType);
    }


}
