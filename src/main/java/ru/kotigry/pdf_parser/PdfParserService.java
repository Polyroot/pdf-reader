package ru.kotigry.pdf_parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.tools.PDFText2HTML;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.Arrays;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class PdfParserService {

    @Value("${files.parsed_files_dir}")
    private String fileDir;

    public StreamingResponseBody getParsedPdf(MultipartFile inputFile) throws IOException {

        File parsedPdfFile = parsePdfFile(inputFile);

        return out -> {
            createZipWithDiplomas(Set.of(parsedPdfFile), out);
            cleanCardsDir();
        };
    }

    private File parsePdfFile(MultipartFile inputFile) throws IOException {
        log.info("input file with name {}", inputFile.getOriginalFilename());

        String htmlText = pdfToHtml(inputFile);

        String fileName = parsedFileName(inputFile);
        File parsedPdfFile = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(parsedPdfFile))){
            writer.append(htmlText);
        }
        return parsedPdfFile;
    }

    private String parsedFileName(MultipartFile inputFile) {
        String fileName = nonNull(inputFile.getOriginalFilename()) ? inputFile.getOriginalFilename() : "pdf";
        return String.format(fileDir + "/%s.html", fileName.substring(0, fileName.lastIndexOf('.')));
    }

    private String pdfToHtml(MultipartFile inputFile) throws IOException {
        PDDocument pdf = PDDocument.load(inputFile.getInputStream());
        PDFText2HTML stripper = new PDFText2HTML();
        return stripper.getText(pdf);
    }

    private void createZipWithDiplomas(Set<File> files, OutputStream out) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {
            // package files
            for (File file : files) {
                //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    IOUtils.copy(fileInputStream, zipOutputStream);
                }
                zipOutputStream.closeEntry();
            }
        }
    }

    public void cleanCardsDir() {
        File diplomaDirectory = new File(fileDir);
        File[] files = diplomaDirectory.listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(File::delete);
        }
    }
}
