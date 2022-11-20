package com.opensearch.util.reader;

import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.EncryptedDocumentException;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import java.util.*;

public class PdfReader implements Reader {
    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                List<String> words = Arrays.asList(text.split(" "));
                Map<String, List<Document>> dict = new HashMap<>();
                for (int i = 0; i < words.size(); i++) {
                    String currentWord = words.get(i);
                    if (currentWord.isEmpty()) continue;
                    List<Document> documents = dict.getOrDefault(currentWord, new ArrayList<>());
                    documents.add(new Document(file.getAbsolutePath(), i, DocumentType.PDF, null));
                    dict.put(currentWord, documents);
                }
                return dict;
            } else {
                throw new EncryptedDocumentException("File is encrypted!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
