package com.opensearch.util.reader;

import com.opensearch.entity.document.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.EncryptedDocumentException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PdfReader extends AbstractReader {

    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        return parseText(readPdf(file), file, regex);
    }

    private String readPdf(File file) {
        try {
            PDDocument document = PDDocument.load(file);
            if (!document.isEncrypted()) {
                throw new EncryptedDocumentException("File is encrypted!");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            return "";
        }
    }

}
