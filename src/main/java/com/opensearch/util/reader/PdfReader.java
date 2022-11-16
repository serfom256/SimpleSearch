package com.opensearch.util.reader;

import com.opensearch.entity.document.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PdfReader implements Reader {
    @Override
    public Map<String, Document> read(File file, List<Character> regex) {
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                System.out.println(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
