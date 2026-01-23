package com.simplesearch.util.reader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.simplesearch.model.internal.document.Document;
import com.simplesearch.model.internal.document.DocumentType;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class PdfReader extends AbstractReader {

    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        return parseText(readPdf(file), file, regex, DocumentType.PDF);
    }

    private String readPdf(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            if (!document.isEncrypted()) {
                log.warn("File {} is encrypted!", file.getAbsolutePath());
                return "";
            }
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            return "";
        }
    }

}
