package com.krish.etl.scheduler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ResourceReader {
    private static final Logger log = LoggerFactory.getLogger(ResourceReader.class);
    public static String readStringResource(String resourceName) {
        InputStream resourceFile = ResourceReader.class.getClassLoader().getResourceAsStream(resourceName);
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (resourceFile, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        } catch (IOException e) {
            log.error("Error reading resource file: {}", resourceName, e);
            throw new RuntimeException(e);
        }
        return textBuilder.toString();
    }
}
