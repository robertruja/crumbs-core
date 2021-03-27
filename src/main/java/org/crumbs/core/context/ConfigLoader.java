package org.crumbs.core.context;

import org.crumbs.core.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigLoader {

    private static Logger LOGGER = Logger.getLogger(ConfigLoader.class);

    public static Map<String, String> loadProperties() {
        Properties properties = new Properties();
        InputStream propertiesStream = ConfigLoader.class.getClassLoader().getResourceAsStream("crumbs.properties");
        try {
            LOGGER.info("Found crumbs.properties, loading config");
            if(propertiesStream != null) {
                properties.load(propertiesStream);
                Map<String, String> propertiesMap = properties.keySet().stream()
                        .map(Object::toString)
                        .collect(Collectors.toMap(key -> key, properties::getProperty));
                return Collections.unmodifiableMap(
                        replaceValues(propertiesMap));
            }
        } catch (IOException e) {
            LOGGER.warn("No crumbs properties found in classpath");
        }
        return null;
    }

    private static Map<String, String> replaceValues(Map<String, String> propertyMap) {
        Map<String, String> replaced = new HashMap<>();
        propertyMap.entrySet().stream()
                .forEach(entry -> {
                    String propertyKey = entry.getKey();
                    String propertyValue = entry.getValue();
                    if(propertyValue.contains("${") &&
                            propertyValue.substring(propertyValue.indexOf("${")).contains("}")) {
                        int start = propertyValue.indexOf("${");
                        int end = propertyValue.indexOf("}");
                        String prefix = propertyValue.substring(0, start);
                        String ref = propertyValue.substring(start + 2, end);
                        String suffix = propertyValue.substring(end + 1);
                        propertyValue = prefix + propertyMap.get(ref) + suffix;
                    }
                    propertyValue = System.getProperty(propertyKey, propertyValue);
                    replaced.put(propertyKey, propertyValue);
                });
        return replaced;
    }
}
