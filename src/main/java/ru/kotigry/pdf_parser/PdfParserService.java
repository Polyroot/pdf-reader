package ru.kotigry.pdf_parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.fit.pdfdom.PDFDomTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public StreamingResponseBody getCards(MultipartFile inputFile) throws IOException {

        String fileName = nonNull(inputFile.getOriginalFilename()) ? inputFile.getOriginalFilename() : "pdf";

        log.info("input file with name {}", fileName);
        PDDocument pdf = PDDocument.load(inputFile.getInputStream());

        File fileDiploma = new File(String.format(fileDir + "/%s.html", fileName.substring(0, fileName.lastIndexOf('.'))));
        Writer output = new PrintWriter(fileDiploma, StandardCharsets.UTF_8);
        new PDFDomTree().writeText(pdf, output);

        output.close();

        return out -> {
            createZipWithDiplomas(Set.of(fileDiploma), out);
            cleanCardsDir();
        };
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


    private File getFile(byte[] parsedPdf, String pdfName) {

        File fileDiploma = new File(String.format(fileDir + "/%s", pdfName));

        try (FileOutputStream stream = new FileOutputStream(fileDiploma)) {
            stream.write(parsedPdf);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return fileDiploma;
    }

//    @NonNull
//    private byte[] parsePdfFile(MultipartFile inputFile) {
//
//        log.info("file parsing started:");
//
//        byte[] content = null;
//
//        try (InputStream fileInputStream = inputFile.getInputStream()) {
//            try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(fileInputStream))) {
//
//                // Create a text extraction renderer
//                LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
//
//                // Note: if you want to re-use the PdfCanvasProcessor, you must call PdfCanvasProcessor.reset()
//                PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
//                parser.processPageContent(pdfDoc.getFirstPage());
//
//                content = strategy.getResultantText().getBytes(StandardCharsets.UTF_8);
//            }
//            log.info("file parsed successfully!");
//        } catch (IOException e) {
//            log.error(e.getLocalizedMessage(), e);
//        }
//        return content;
//    }

    public void cleanCardsDir() {
        File diplomaDirectory = new File(fileDir);
        File[] files = diplomaDirectory.listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(File::delete);
        }
    }
}
