package com.opensearch.util.reader;

import com.opensearch.entity.document.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DocxReader extends AbstractReader {
    @Override
    public Map<String, List<Document>> read(File file, List<Character> regex) {
        return parseText(readDocx(file), file, regex);
    }

    private String readDocx(File file) {
        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());

            XWPFDocument document = new XWPFDocument(fis);

            String resultStr = "";

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph para : paragraphs) {
                resultStr = resultStr + " " + para.getText();
            }
            fis.close();
            return resultStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
