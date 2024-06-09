package com.simplesearch.api;

import com.simplesearch.model.request.IndexingRequest;
import com.simplesearch.model.response.IndexingResponse;
import com.simplesearch.service.files.impl.LocalFileIndexingService;
import com.simplesearch.service.files.impl.MultipartIndexingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@Log4j2
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FilesController {

    private final LocalFileIndexingService localFileService;
    private final MultipartIndexingService multipartFileService;

    @PostMapping("/save")
    public ResponseEntity<IndexingResponse> indexLocalFile(@RequestBody IndexingRequest document) {
        log.debug(document);
        return ResponseEntity.ok(localFileService.indexFile(document));
    }

    @PostMapping(path = "/saveMultipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IndexingResponse> indexMultipartFile(@RequestParam("file") MultipartFile document) {
        log.debug(document);
        return ResponseEntity.ok(multipartFileService.indexFile(document));
    }
}
