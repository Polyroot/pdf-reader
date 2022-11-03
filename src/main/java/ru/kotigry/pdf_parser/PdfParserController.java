package ru.kotigry.pdf_parser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


@RestController
@Slf4j
public class PdfParserController {

    @Autowired
    private PdfParserService pdfParserService;

    @SneakyThrows
    @PostMapping(value="/inputPdf/get", produces="application/zip")
    public ResponseEntity<StreamingResponseBody> getCards(@RequestParam("inputPdf") MultipartFile inputFile) {

        return ResponseEntity
                .ok()
                .body(pdfParserService.getCards(inputFile));
    }

}
