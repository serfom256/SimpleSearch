package com.opensearch.test;

import com.opensearch.core.LoadBalancer;
import com.opensearch.entity.document.Document;
import com.opensearch.entity.document.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class Test {


    private final LoadBalancer loadBalancer;

    @Autowired
    public Test(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

//    @PostConstruct
    public void index() {
        for (int i = 0; i < 3; i++) {
            Map<String, List<Document>> map = new HashMap<>();
            for (int k = 0; k < 1_000_000; k++) {
                map.put(generateString(4, 15), Arrays.asList(new Document("/path/to/file", (int) (Math.random() * 10000), DocumentType.JSON, "{\"cnt\":\"5\"}")));

            }
            System.out.println("map filled");
            loadBalancer.createIndex(map);
        }

    }

    private String generateString(int minLen, int maxLen) {
        int leftLimit = 97;
        int rightLimit = 122;
        int len = (int) ((Math.random() * (maxLen - minLen)) + minLen);
        StringBuilder s = new StringBuilder();
        new Random().ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(len)
                .forEach(s::appendCodePoint);
        return s.toString();
    }
}
