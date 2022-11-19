package com.opensearch.common;

import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
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
