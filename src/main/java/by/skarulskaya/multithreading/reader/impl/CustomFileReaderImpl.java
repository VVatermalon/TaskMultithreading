package by.skarulskaya.multithreading.reader.impl;

import by.skarulskaya.multithreading.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomFileReaderImpl {
    static final Logger logger = LogManager.getLogger();

    public List<String> readFromFile(String src) throws CustomException {
        try {
            ArrayList<String> output = new ArrayList<>(0);
            if (src == null) {
                logger.error("Null parameter");
                return output;
            }
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(src);
            if (resource == null) {
                logger.error("Null parameter");
                return output;
            }
            output = Files.lines(Paths.get(resource.toURI()))
                    .collect(Collectors.toCollection(ArrayList::new));
            return output;
        } catch (IOException | URISyntaxException e) {
            logger.error(e);
            throw new CustomException("Cannot read from file " + src, e);
        }
    }
}
