package com.simplesearch.common;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class PropertiesPrinter {
    public void printProperties(Map<String, Object> props) {
        final String propsInfo = "property: [%s] = [%s]";
        log.info("====================================================================================");
        props.forEach((a, b) -> {
            log.info(String.format(propsInfo, a, b));
        });
        log.info("====================================================================================");
    }
}
