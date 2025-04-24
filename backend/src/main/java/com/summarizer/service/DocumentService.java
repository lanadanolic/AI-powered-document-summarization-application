package com.summarizer.service;

import com.summarizer.model.Document;
import com.summarizer.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${openrouter.api.url}")
    private String openRouterApiUrl;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    public Document uploadAndSummarize(MultipartFile file, String title) throws IOException {
        Document document = new Document();
        document.setTitle(title);
        document.setFileType(file.getContentType());
        document.setUploadDate(LocalDateTime.now());
        
        // Extract text based on file type
        String content = extractText(file);
        document.setContent(content);
        
        // Generate summary using OpenRouter
        String summary = generateSummary(content);
        document.setSummary(summary);
        document.setSummaryDate(LocalDateTime.now());
        
        return documentRepository.save(document);
    }

    private String extractText(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType.equals("application/pdf")) {
            try (PDDocument pdf = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(pdf);
            }
        } else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                return extractor.getText();
            }
        } else if (contentType.equals("text/plain")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        throw new IllegalArgumentException("Unsupported file type: " + contentType);
    }

    private String generateSummary(String content) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(openRouterApiUrl + "/chat/completions");
            
            // Prepare the request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "meta-llama/llama-4-maverick:free");
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful assistant that summarizes text."),
                Map.of("role", "user", "content", "Please provide a concise summary of the following text, highlighting the key points and main ideas: " + content)
            ));
            
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(requestBody));
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + openRouterApiKey);
            httpPost.setHeader("HTTP-Referer", "http://localhost:3000");
            httpPost.setHeader("X-Title", "Document Summarizer");
            
            // Execute the request and parse the response
            return httpClient.execute(httpPost, response -> {
                String responseBody = new String(response.getEntity().getContent().readAllBytes());
                Map<String, Object> jsonResponse = objectMapper.readValue(responseBody, Map.class);
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) jsonResponse.get("choices");
                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            });
        }
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAllByOrderByUploadDateDesc();
    }

    public Document getDocument(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));
    }
} 