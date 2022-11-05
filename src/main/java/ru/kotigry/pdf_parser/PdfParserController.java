package ru.kotigry.pdf_parser;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@Slf4j
@RequiredArgsConstructor
public class PdfParserController {

    @NonNull
    private final PdfParserService pdfParserService;

    @SneakyThrows
    @PostMapping(value="/inputPdf/get", produces="application/zip")
    public ResponseEntity<StreamingResponseBody> getParsedPdf(@RequestParam("inputPdf") MultipartFile inputFile) {

        return ok().body(pdfParserService.getParsedPdf(inputFile));
    }

}
