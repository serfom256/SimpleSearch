package com.simplesearch.util.reader;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.simplesearch.model.internal.document.Document;
import com.simplesearch.model.internal.document.DocumentType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DocxReader extends AbstractReader {

    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        return parseText(readDocx(file), file, regex, DocumentType.DOCX);
    }

    private String readDocx(File file) {
        try (FileInputStream fis = new FileInputStream(file.getAbsolutePath())) {
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            StringBuilder result = new StringBuilder();
            for (XWPFParagraph pair : paragraphs) {
                result.append(pair.getText());
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
