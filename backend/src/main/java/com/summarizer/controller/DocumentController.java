package com.summarizer.controller;

import com.summarizer.model.Document;
import com.summarizer.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title) throws IOException {
        return ResponseEntity.ok(documentService.uploadAndSummarize(file, title));
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocument(id));
    }

    @GetMapping("/{id}/download/txt")
    public ResponseEntity<byte[]> downloadAsTxt(@PathVariable Long id) {
        try {
            log.info("Downloading TXT for document ID: {}", id);
            Document document = documentService.getDocument(id);
            String content = String.format("Title: %s\n\nSummary:\n%s\n\nGenerated on: %s",
                document.getTitle(),
                document.getSummary(),
                document.getSummaryDate().toString());
            
            byte[] documentBytes = content.getBytes(StandardCharsets.UTF_8);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(document.getTitle() + "_summary.txt")
                    .build());
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            headers.setContentLength(documentBytes.length);
            
            log.info("Successfully generated TXT for document ID: {} with size: {} bytes", id, documentBytes.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(documentBytes);
        } catch (Exception e) {
            log.error("Error generating TXT for document ID: " + id, e);
            throw new RuntimeException("Error generating TXT file: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{id}/download/docx")
    public ResponseEntity<byte[]> downloadAsDocx(@PathVariable Long id) throws IOException {
        try {
            log.info("Downloading DOCX for document ID: {}", id);
            Document document = documentService.getDocument(id);
            
            XWPFDocument docx = new XWPFDocument();
            
            // Add title
            XWPFParagraph titleParagraph = docx.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setText(document.getTitle());
            titleRun.addBreak();
            titleRun.addBreak();
            
            // Add summary
            XWPFParagraph summaryParagraph = docx.createParagraph();
            XWPFRun summaryRun = summaryParagraph.createRun();
            summaryRun.setFontSize(12);
            summaryRun.setText(document.getSummary());
            summaryRun.addBreak();
            summaryRun.addBreak();
            
            // Add generation date
            XWPFParagraph dateParagraph = docx.createParagraph();
            XWPFRun dateRun = dateParagraph.createRun();
            dateRun.setItalic(true);
            dateRun.setFontSize(10);
            dateRun.setText("Generated on: " + document.getSummaryDate().toString());
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            docx.write(out);
            docx.close();
            
            byte[] documentBytes = out.toByteArray();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(document.getTitle() + "_summary.docx")
                    .build());
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            headers.setContentLength(documentBytes.length);
            
            log.info("Successfully generated DOCX for document ID: {} with size: {} bytes", id, documentBytes.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(documentBytes);
        } catch (Exception e) {
            log.error("Error generating DOCX for document ID: " + id, e);
            throw new RuntimeException("Error generating DOCX file: " + e.getMessage(), e);
        }
    }
} 