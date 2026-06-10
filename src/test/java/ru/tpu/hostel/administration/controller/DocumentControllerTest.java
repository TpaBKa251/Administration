package ru.tpu.hostel.administration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.entity.DocumentType;
import ru.tpu.hostel.administration.service.DocumentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DocumentService documentService;

    @Test
    void addDocumentWithSuccess() throws Exception {
        when(documentService.addDocument(any())).thenReturn(TestData.documentResponseDto());

        mockMvc.perform(post("/documents")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestData.defaultDocumentRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestData.DOCUMENT_ID.toString()));
    }

    @Test
    void editDocumentWithSuccess() throws Exception {
        when(documentService.editDocument(any())).thenReturn(TestData.documentResponseDto());

        mockMvc.perform(patch("/documents/edit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestData.documentEditRequestDto(TestData.DOCUMENT_ID))))
                .andExpect(status().isOk());
    }

    @Test
    void getDocumentByIdWithSuccess() throws Exception {
        when(documentService.getDocumentById(TestData.DOCUMENT_ID)).thenReturn(TestData.documentResponseDto());

        mockMvc.perform(get("/documents/get/by/id/{id}", TestData.DOCUMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestData.DOCUMENT_ID.toString()));
    }

    @Test
    void getAllDocumentsByUserWithSuccess() throws Exception {
        when(documentService.getAllUserDocuments(TestData.USER_ID))
                .thenReturn(List.of(TestData.documentResponseDto()));

        mockMvc.perform(get("/documents/get/all/user/{userId}", TestData.USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllDocumentsWithSuccess() throws Exception {
        when(documentService.getAllUserDocuments(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(List.of(TestData.documentResponseDto()));

        mockMvc.perform(get("/documents/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getDocumentByTypeWithSuccess() throws Exception {
        when(documentService.getUserDocumentsByType(TestData.USER_ID, DocumentType.FLUOROGRAPHY))
                .thenReturn(TestData.documentResponseDto());

        mockMvc.perform(get("/documents/get/type/{documentType}/user/{userId}",
                        DocumentType.FLUOROGRAPHY, TestData.USER_ID))
                .andExpect(status().isOk());
    }
}
